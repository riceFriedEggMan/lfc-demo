package com.rice.msg.tools;

import com.rice.msg.common.conf.SendMsgConf;
import com.rice.msg.constant.Constants;
import com.rice.msg.entity.TSourceQuota;
import com.rice.msg.mapper.TGlobalQuotaMapper;
import com.rice.msg.mapper.TSourceQuotaMapper;
import com.rice.msg.model.GlobalQuotaModel;
import com.rice.msg.model.SourceQuotaModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RateLimitServiceImpl implements RateLimitService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SendMsgConf sendMsgConf;
    @Autowired
    private TSourceQuotaMapper sourceQuotaMapper;
    @Autowired
    private TGlobalQuotaMapper globalQuotaMapper;


    @Override
    public boolean isRequestAllowed(String sourceId, Integer channel, boolean isTimerMsg) {
        int numLimit = 0;
        int unit = 0;


        String quotaConf = getQuotaConfWithCache(sourceId, channel, isTimerMsg);
        if (StringUtils.isBlank(quotaConf) || quotaConf.split("_").length != 2) {
            return false;
        }
        String[] parts = quotaConf.split("_");
        numLimit = Integer.parseInt(parts[0]);
        unit = Integer.parseInt(parts[1]);

        String keyId = String.format(Constants.REDIS_KEY_RATE_LIMIT_COUNT + ":%s:%d", sourceId, channel);
        if (isTimerMsg){
            // 定时消息需要用到的key
            keyId = "";
        }

        return checkAllowed(keyId, numLimit, unit);
    }



    private String getQuotaConfWithCache(String sourceId, Integer channel, boolean isTimerMsg) {
        String sourceKey = Constants.REDIS_KEY_SOURCE_QUOTA + sourceId + channel;
        String cacheQt = redisTemplate.opsForValue().get(sourceKey);

        String reQuotaStr = "";
        if (StringUtils.isNotBlank(cacheQt) && sendMsgConf.isOpenCache()) {
            reQuotaStr = cacheQt;
            return reQuotaStr;
        }
        SourceQuotaModel sourceQuotaModel = sourceQuotaMapper.getSourceQuota(channel, sourceId);
        if (sourceQuotaModel != null) {
            reQuotaStr = sourceQuotaModel.getNum() + "_" + sourceQuotaModel.getUnit();
        }else{
            String channelKey = Constants.REDIS_KEY_CHANNEL_QUOTA + channel;
            String channelQt = redisTemplate.opsForValue().get(channelKey);
            if (StringUtils.isNotBlank(channelQt) && sendMsgConf.isOpenCache()) {
                return channelQt;
            }else{
                GlobalQuotaModel globalQuotaModel = globalQuotaMapper.getGlobalQuota(channel);
                if (globalQuotaModel != null) {
                    reQuotaStr = globalQuotaModel.getNum() + "_" + globalQuotaModel.getUnit();
                    if (sendMsgConf.isOpenCache()){
                        redisTemplate.opsForValue().set(channelKey, reQuotaStr, Duration.ofHours(1));
                    }
                    return reQuotaStr;
                }

            }
        }
        if (StringUtils.isNotBlank(reQuotaStr) && sendMsgConf.isOpenCache()) {
            redisTemplate.opsForValue().set(sourceKey, reQuotaStr, Duration.ofSeconds(30));
        }
        return reQuotaStr;
    }

    private boolean checkAllowed(String keyId, int numLimit, int unit) {
        long currentStart = System.currentTimeMillis() / 1000;
        String key = String.format(keyId + ":%d", currentStart);
        Long count = redisTemplate.opsForValue().increment(key, 1);
        if (count == null){
            log.error("计数失败");
            return false;
        }
        if (count == 1){
            long expire = unit / 1000;
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return count <= numLimit;
    }
}

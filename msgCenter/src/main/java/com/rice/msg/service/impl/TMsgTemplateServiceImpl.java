package com.rice.msg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.msg.common.conf.SendMsgConf;
import com.rice.msg.constant.Constants;
import com.rice.msg.entity.TMsgTemplate;
import com.rice.msg.mapper.TMsgTemplateMapper;
import com.rice.msg.service.TMsgTemplateService;
import com.rice.msg.utils.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 消息模板表(TMsgTemplate)表服务实现类
 *
 * @author makejava
 * @since 2026-06-07 12:25:00
 */
@Service
public class TMsgTemplateServiceImpl extends ServiceImpl<TMsgTemplateMapper, TMsgTemplate> implements TMsgTemplateService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private SendMsgConf sendMsgConf;


    @Override
    public TMsgTemplate GetTemplateWithCache(String templateId) {
        String key = Constants.REDIS_KEY_TEMPLATE + templateId;
        String object = redisTemplate.opsForValue().get(key);
        TMsgTemplate tMsgTemplate = null;
        if (StringUtils.isNotBlank(object) && sendMsgConf.isOpenCache()) {
            tMsgTemplate = JSONUtil.parseObject(object, TMsgTemplate.class);
            if (tMsgTemplate != null) {
                return tMsgTemplate;
            }
        }

        tMsgTemplate = getBaseMapper().getTemplateById(templateId);
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonString(tMsgTemplate), Duration.ofSeconds(30));
        return tMsgTemplate;
    }
}

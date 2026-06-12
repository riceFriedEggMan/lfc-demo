package com.rice.msg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.model.ResponseEntity;
import com.rice.msg.common.conf.SendMsgConf;
import com.rice.msg.constant.Constants;
import com.rice.msg.entity.TMsgTemplate;
import com.rice.msg.enums.TemplateStatus;
import com.rice.msg.exception.BusinessException;
import com.rice.msg.exception.ErrorCode;
import com.rice.msg.mapper.TMsgTemplateMapper;
import com.rice.msg.service.TMsgTemplateService;
import com.rice.msg.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

/**
 * 消息模板表(TMsgTemplate)表服务实现类
 *
 * @author makejava
 * @since 2026-06-07 12:25:00
 */
@Service
@Slf4j
public class TMsgTemplateServiceImpl extends ServiceImpl<TMsgTemplateMapper, TMsgTemplate> implements TMsgTemplateService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private SendMsgConf sendMsgConf;
    @Autowired
    private TMsgTemplateMapper tMsgTemplateMapper;


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

    @Override
    public ResponseEntity addTemplate(TMsgTemplate tMsgTemplate) {
        if (tMsgTemplate.getChannel() == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "模板channel错误");
        }
        String templateId = UUID.randomUUID().toString();
        tMsgTemplate.setTemplateId(templateId);
        tMsgTemplate.setRelTemplateId(UUID.randomUUID().toString());
        tMsgTemplate.setStatus(TemplateStatus.TEMPLATE_STATUS_PENDING.getStatus());
        save(tMsgTemplate);
        return ResponseEntity.ok(templateId);
    }

    @Override
    public ResponseEntity delete(String templateId) {
        tMsgTemplateMapper.deleteTemplateById(templateId);
        return ResponseEntity.ok(templateId);
    }

    @Override
    @Transactional
    public ResponseEntity updateTemplate(TMsgTemplate tMsgTemplate) {
        log.info("更新模板: templateId={}", tMsgTemplate.getTemplateId());
        log.info("需要更新的数据: {}", tMsgTemplate);

        int update = tMsgTemplateMapper.update(tMsgTemplate);

        log.info("更新结果: 影响了 {} 条记录", update);
        return ResponseEntity.ok(tMsgTemplate.getId());
    }

    @Override
    public ResponseEntity getTemplateById(String templateId) {
        String key = Constants.REDIS_KEY_TEMPLATE + templateId;
        String cacheTp = redisTemplate.opsForValue().get(key);
        TMsgTemplate tMsgTemplate = null;
        if (StringUtils.isNotBlank(cacheTp) && sendMsgConf.isOpenCache()) {
            tMsgTemplate = JSONUtil.parseObject(cacheTp, TMsgTemplate.class);
            if (tMsgTemplate != null) {
                return ResponseEntity.ok(tMsgTemplate);
            }
        }

        tMsgTemplate = tMsgTemplateMapper.getTemplateById(templateId);

        redisTemplate.opsForValue().set(key, JSONUtil.toJsonString(tMsgTemplate), Duration.ofSeconds(30));

        return ResponseEntity.ok(tMsgTemplate);
    }
}

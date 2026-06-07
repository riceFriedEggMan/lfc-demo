package com.rice.msg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.msg.entity.TMsgTemplate;


/**
 * 消息模板表(TMsgTemplate)表服务接口
 *
 * @author makejava
 * @since 2026-06-07 12:25:00
 */
public interface TMsgTemplateService extends IService<TMsgTemplate> {

    TMsgTemplate GetTemplateWithCache(String templateId);
}

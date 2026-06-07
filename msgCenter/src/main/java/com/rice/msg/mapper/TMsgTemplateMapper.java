package com.rice.msg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rice.msg.entity.TMsgTemplate;


/**
 * 消息模板表(TMsgTemplate)表数据库访问层
 *
 * @author makejava
 * @since 2026-06-07 12:25:00
 */
public interface TMsgTemplateMapper extends BaseMapper<TMsgTemplate> {

    TMsgTemplate getTemplateById(String templateId);
}

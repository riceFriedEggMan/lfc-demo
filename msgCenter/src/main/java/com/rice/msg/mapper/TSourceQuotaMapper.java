package com.rice.msg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rice.msg.entity.TSourceQuota;
import com.rice.msg.model.SourceQuotaModel;


/**
 * 业务限额表(TSourceQuota)表数据库访问层
 *
 * @author makejava
 * @since 2026-06-07 12:29:44
 */
public interface TSourceQuotaMapper extends BaseMapper<TSourceQuota> {

    SourceQuotaModel getSourceQuota(Integer channel, String sourceId);
}

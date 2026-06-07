package com.rice.msg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rice.msg.entity.TGlobalQuota;
import com.rice.msg.model.GlobalQuotaModel;


/**
 * 全局限额表(TGlobalQuota)表数据库访问层
 *
 * @author makejava
 * @since 2026-06-07 12:29:58
 */
public interface TGlobalQuotaMapper extends BaseMapper<TGlobalQuota> {

    GlobalQuotaModel getGlobalQuota(Integer channel);
}

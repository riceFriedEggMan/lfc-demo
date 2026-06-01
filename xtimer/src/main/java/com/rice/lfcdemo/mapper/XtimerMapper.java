package com.rice.lfcdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rice.lfcdemo.entity.Xtimer;


/**
 * Timer 信息(Xtimer)表数据库访问层
 *
 * @author makejava
 * @since 2026-06-01 15:08:19
 */
public interface XtimerMapper extends BaseMapper<Xtimer> {

    void save(Xtimer xtimer);
}

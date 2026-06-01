package com.rice.lfcdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.lfcdemo.entity.Xtimer;
import com.rice.lfcdemo.entity.dto.TimerDTO;


/**
 * Timer 信息(Xtimer)表服务接口
 *
 * @author makejava
 * @since 2026-06-01 15:08:21
 */
public interface XtimerService extends IService<Xtimer> {

    Long createTimer(TimerDTO timerDTO);
}

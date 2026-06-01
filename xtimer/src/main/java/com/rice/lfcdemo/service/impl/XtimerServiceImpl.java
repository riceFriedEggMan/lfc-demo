package com.rice.lfcdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.entity.Xtimer;
import com.rice.lfcdemo.entity.dto.TimerDTO;
import com.rice.lfcdemo.exception.BusinessException;
import com.rice.lfcdemo.exception.ErrorCode;
import com.rice.lfcdemo.mapper.XtimerMapper;
import com.rice.lfcdemo.service.XtimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

/**
 * Timer 信息(Xtimer)表服务实现类
 *
 * @author makejava
 * @since 2026-06-01 15:08:23
 */
@Service
public class XtimerServiceImpl extends ServiceImpl<XtimerMapper, Xtimer> implements XtimerService {

    @Autowired
    private XtimerMapper xtimerMapper;
    @Override
    public Long createTimer(TimerDTO timerDTO) {
        boolean validExpression = CronExpression.isValidExpression(timerDTO.getCron());
        if (!validExpression) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid cron");
        }

        Xtimer xtimer = Xtimer.voToObj(timerDTO);
        if (xtimer == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        xtimerMapper.save(xtimer);
        return xtimer.getTimerId();
    }
}

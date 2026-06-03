package com.rice.lfcdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.entity.Xtimer;
import com.rice.lfcdemo.entity.dto.TimerDTO;
import com.rice.lfcdemo.enums.TimerStatus;
import com.rice.lfcdemo.exception.BusinessException;
import com.rice.lfcdemo.exception.ErrorCode;
import com.rice.lfcdemo.manage.MigratorManager;
import com.rice.lfcdemo.mapper.XtimerMapper;
import com.rice.lfcdemo.model.ResponseEntity;
import com.rice.lfcdemo.redis.ReentrantDistributeLock;
import com.rice.lfcdemo.service.XtimerService;
import com.rice.lfcdemo.utils.TimerUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private MigratorManager migratorManager;
    @Autowired
    private ReentrantDistributeLock reentrantDistributeLock;

    public static final int defaultGapSeconds = 3;

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

    @Override
    public ResponseEntity enableXtimer(String app, Long timerId) {
        String value = TimerUtils.GetTokenStr();
        boolean lock = reentrantDistributeLock.lock(
                TimerUtils.GetEnableLockKey(app),
                value,
                defaultGapSeconds
        );
        if (!lock) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"激活/去激活操作过于频繁，请稍后再试！");
        }


        doEnableTimer(timerId);

        return ResponseEntity.ok();
    }

    @Transactional
    public void doEnableTimer(Long timerId) {
        Xtimer xtimer = xtimerMapper.getTimerById(timerId);
        if (xtimer == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "激活失败，timer不存在：timerId" + timerId);
        }
        if(xtimer.getStatus() == TimerStatus.Enable.getStatus()){
            log.warn("Timer非Unable状态，激活失败，timerId:" + xtimer.getTimerId());
        }
        xtimer.setStatus(TimerStatus.Enable.getStatus());
        xtimerMapper.update(xtimer);
        migratorManager.migrateTimer(xtimer);
    }
}

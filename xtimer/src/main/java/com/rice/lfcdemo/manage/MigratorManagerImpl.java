package com.rice.lfcdemo.manage;

import com.rice.lfcdemo.common.conf.MigratorAppConf;
import com.rice.lfcdemo.entity.TaskModel;
import com.rice.lfcdemo.entity.Xtimer;
import com.rice.lfcdemo.enums.TaskStatus;
import com.rice.lfcdemo.enums.TimerStatus;
import com.rice.lfcdemo.exception.BusinessException;
import com.rice.lfcdemo.exception.ErrorCode;
import com.rice.lfcdemo.mapper.TimerTaskMapper;
import com.rice.lfcdemo.redis.TaskCache;
import com.rice.lfcdemo.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MigratorManagerImpl implements MigratorManager {
    @Autowired
    private MigratorAppConf migratorAppConf;
    @Autowired
    private TimerTaskMapper timerTaskMapper;
    @Autowired
    private TaskCache taskCache;

    @Override
    public void migrateTimer(Xtimer xtimer) {
        // 2. 校验状态
        if (xtimer.getStatus().equals(TimerStatus.Unable.getStatus())){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"Timer非Enable状态，迁移失败，timerId:"+xtimer.getTimerId());
        }

        // 3.取得批量的执行时机
        CronExpression cronExpression;
        try {
            cronExpression = new CronExpression(xtimer.getCron());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"解析cron表达式失败："+xtimer.getCron());
        }
        Date now = new Date();
        Date end = TimerUtils.GetForwardTwoMigrateStepEnd(now, migratorAppConf.getMigrateStepMinutes());
        List<Long> times = TimerUtils.GetCronNextBetween(cronExpression, now, end);
        if (CollectionUtils.isEmpty(times) ){
            log.warn("获取执行时机 executeTimes 为空");
            return;
        }
        List<TaskModel> taskModels = batchTaskFromTimer(xtimer, times);
        // 执行时机加入数据库
        // 基于 timer_id + run_timer 唯一键，保证任务不被重复插入
        timerTaskMapper.batchSave(taskModels);

        // 执行时机加入 redis ZSet
        // 使用sessionCallback进行批量写入，需要RedisOperations的mutil方法和exec方法
        boolean cacheRes = taskCache.cacheTaskList(taskModels);
        if(!cacheRes){
            log.error("Zset存储taskList失败");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"ZSet存储taskList失败，timerId:" + xtimer.getTimerId());
        }

    }

    private List<TaskModel> batchTaskFromTimer(Xtimer xtimer, List<Long> times) {
        if (xtimer == null || CollectionUtils.isEmpty(times)){
            return null;
        }

        List<TaskModel> taskModels = new ArrayList<>();
        for (Long time : times) {
            TaskModel taskModel = new TaskModel();
            taskModel.setApp(xtimer.getApp());
            taskModel.setTimerId(xtimer.getTimerId());
            taskModel.setRunTimer(time);
            taskModel.setStatus(TaskStatus.NotRun.getStatus());
            taskModels.add(taskModel);
        }
        return taskModels;
    }
}

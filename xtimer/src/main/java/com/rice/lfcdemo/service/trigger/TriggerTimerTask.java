package com.rice.lfcdemo.service.trigger;

import com.rice.lfcdemo.common.conf.TriggerAppConf;
import com.rice.lfcdemo.entity.TaskModel;
import com.rice.lfcdemo.mapper.TimerTaskMapper;
import com.rice.lfcdemo.redis.TaskCache;
import lombok.extern.slf4j.Slf4j;
import com.rice.lfcdemo.enums.TaskStatus;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class TriggerTimerTask extends TimerTask {

    TriggerAppConf triggerAppConf;

    TaskCache taskCache;

    TimerTaskMapper timerTaskMapper;

    TriggerPoolTask triggerPoolTask;

    private CountDownLatch latch;

    private Long count = 0L;

    private Date startTime;

    private Date endTime;

    private String minuteBucketKey;


    public TriggerTimerTask(TriggerAppConf triggerAppConf,TriggerPoolTask triggerPoolTask,
                            TaskCache taskCache,TimerTaskMapper taskMapper,CountDownLatch latch,
                            Date startTime, Date endTime, String minuteBucketKey) {
        this.triggerAppConf = triggerAppConf;
        this.triggerPoolTask = triggerPoolTask;
        this.taskCache = taskCache;
        this.timerTaskMapper = taskMapper;
        this.latch = latch;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minuteBucketKey = minuteBucketKey;
    }

    @Override
    public void run() {
        Date tStart = new Date(startTime.getTime() + count * triggerAppConf.getZrangeGapSeconds() * 1000L);
        if (tStart.compareTo(endTime) > 0) {
            latch.countDown();
            return;
        }
        try {
            handleBatch(tStart, new Date(tStart.getTime() + triggerAppConf.getZrangeGapSeconds() * 1000L));
        } catch (Exception e) {
            log.error("handleBatch Error. minuteBucketKey"+minuteBucketKey+",tStartTime:"+startTime+",e:",e);
        }
        count++;
    }

    private void handleBatch(Date tStart, Date date) {
       List<TaskModel> taskModels = getTasksByTime(tStart, date);
        if (CollectionUtils.isEmpty(taskModels)){
            return;
        }
        for (TaskModel taskModel : taskModels) {
            try {
                if (taskModel == null){
                    continue;
                }
                triggerPoolTask.run(taskModel);
            } catch (Exception e) {
                log.error("executor run task error,task"+taskModel.toString());
            }
        }
    }

    private List<TaskModel> getTasksByTime(Date tStart, Date end) {
        List<TaskModel> taskModels = new ArrayList<>();

        // 走缓存取数据，没有再走数据库
        try {
            taskModels = taskCache.getTasksFromCache(minuteBucketKey, tStart.getTime(), end.getTime());
        } catch (Exception e) {
            log.error("getTasksFromCache error: " ,e);
            try {
                taskModels = timerTaskMapper.getTasksByTimeRange(tStart.getTime(), end.getTime(), TaskStatus.NotRun.getStatus());
            } catch (Exception ex) {
                log.error("getTasksByConditions error: " ,ex);
            }
        }
        return taskModels;
    }
}

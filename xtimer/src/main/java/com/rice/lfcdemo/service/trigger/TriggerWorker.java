package com.rice.lfcdemo.service.trigger;

import com.rice.lfcdemo.common.conf.TriggerAppConf;
import com.rice.lfcdemo.mapper.TimerTaskMapper;
import com.rice.lfcdemo.redis.TaskCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class TriggerWorker {

    @Autowired
    TriggerAppConf triggerAppConf;

    @Autowired
    TriggerPoolTask triggerPoolTask;

    @Autowired
    TaskCache taskCache;

    @Autowired
    TimerTaskMapper taskMapper;

    public void work(String minuteBucketKey) {
        Date startTime = getStartMinute(minuteBucketKey);
        Date endTime = new Date(startTime.getTime() + 1000 * 60);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Timer timer = new Timer("Timer");
        TriggerTimerTask triggerTimerTask = new TriggerTimerTask(
                triggerAppConf,
                triggerPoolTask,
                taskCache,
                taskMapper,
                countDownLatch,
                startTime,
                endTime,
                minuteBucketKey
        );
        timer.scheduleAtFixedRate(triggerTimerTask, 0, triggerAppConf.getZrangeGapSeconds()*1000L);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("执行TriggerTimerTask异常中断，task:"+triggerTimerTask);
        }finally {
            timer.cancel();
        }
    }

    private Date getStartMinute(String minuteBucketKey) {
        return null;
    }
}

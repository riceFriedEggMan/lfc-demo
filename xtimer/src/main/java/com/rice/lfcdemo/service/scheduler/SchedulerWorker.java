package com.rice.lfcdemo.service.scheduler;

import com.rice.lfcdemo.common.conf.SchedulerAppConf;
import com.rice.lfcdemo.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
public class SchedulerWorker {

    @Autowired
    private SchedulerAppConf schedulerAppConf;
    @Autowired
    private SchedulerTask schedulerTask;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Scheduled(fixedRate = 1000)
    public void scheduledTask(){
        log.info("任务开始执行时间" + LocalDateTime.now());
        handleSlices();
    }

    private void handleSlices() {
        int bucketsNumTemp = schedulerAppConf.getBucketsNum();
        String num = stringRedisTemplate.opsForValue().get(TimerUtils.GetWorkerNumKey());
        int bucketNumBatch = num == null ? 0 : Integer.parseInt(num);
        int bucketNums = Math.max(bucketsNumTemp, bucketNumBatch);
        for (int i = 0; i < bucketNums; i++){
            handleSlice(i);
        }
    }

    private void handleSlice(int buketId) {
        Date now = new Date();
        Date preTime = new Date(now.getTime() - 60000);

        try {
            schedulerTask.asyncHandleSlice(preTime, buketId);
        } catch (Exception e) {
            log.error("[handle slice] submit nowPreMin task failed, err:",e);
        }

        try {
            schedulerTask.asyncHandleSlice(now, buketId);
        } catch (Exception e) {
            log.error("[handle slice] submit nowPreMin task failed, err:",e);
        }


    }
}

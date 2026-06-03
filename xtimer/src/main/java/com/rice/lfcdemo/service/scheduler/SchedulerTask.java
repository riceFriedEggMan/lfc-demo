package com.rice.lfcdemo.service.scheduler;

import com.rice.lfcdemo.common.conf.SchedulerAppConf;
import com.rice.lfcdemo.redis.ReentrantDistributeLock;
import com.rice.lfcdemo.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.PrimitiveIterator;

@Component
@Slf4j
public class SchedulerTask {

    @Autowired
    private ReentrantDistributeLock reentrantDistributeLock;
    @Autowired
    private SchedulerAppConf schedulerAppConf;

    @Async("schedulerPool")
    public void asyncHandleSlice(Date time, int bucketId) {
        log.info("任务开始");
        String value = TimerUtils.GetTokenStr();
        boolean lock = reentrantDistributeLock.lock(
                TimerUtils.GetTimeBucketLockKey(time, bucketId),
                value,
                schedulerAppConf.getTryLockSeconds());
        if (!lock) {
            log.info("asyncHandleSlice 获取分布式锁失败");
            return;
        }
        log.info("get scheduler lock success, key: %s", TimerUtils.GetTimeBucketLockKey(time, bucketId));

        // 触发器执行

//        reentrantDistributeLock.expireLock(
//                TimerUtils.GetTimeBucketLockKey(time, bucketId),
//                value,
//                schedulerAppConf.getSuccessExpireSeconds());

        log.info("end executeAsync");
    }
}

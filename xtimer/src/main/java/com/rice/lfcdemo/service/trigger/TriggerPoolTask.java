package com.rice.lfcdemo.service.trigger;

import com.rice.lfcdemo.entity.TaskModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TriggerPoolTask {

    @Async("triggerPool")
    public void run(TaskModel taskModel) {
        if (taskModel == null) {
            return;
        }
        log.info("start runExecutor");
        // 执行器执行
        log.info("end executeAsync");
    }
}

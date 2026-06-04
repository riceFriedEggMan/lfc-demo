package com.rice.lfcdemo.service.trigger;

import com.rice.lfcdemo.entity.TaskModel;
import com.rice.lfcdemo.service.executor.ExecutorWorker;
import com.rice.lfcdemo.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TriggerPoolTask {

    @Autowired
    private ExecutorWorker executorWorker;


    @Async("triggerPool")
    public void run(TaskModel taskModel) {
        if (taskModel == null) {
            return;
        }
        log.info("start runExecutor");
        // 执行器执行
        executorWorker.work(TimerUtils.UnionTimerIDUnix(taskModel.getTimerId(), taskModel.getRunTimer()));
        log.info("end executeAsync");
    }
}

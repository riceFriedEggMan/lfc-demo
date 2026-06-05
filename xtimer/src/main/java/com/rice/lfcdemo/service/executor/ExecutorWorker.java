package com.rice.lfcdemo.service.executor;

import com.rice.lfcdemo.entity.TaskModel;
import com.rice.lfcdemo.entity.Xtimer;
import com.rice.lfcdemo.entity.dto.TimerDTO;
import com.rice.lfcdemo.entity.param.NotifyHTTPParam;
import com.rice.lfcdemo.enums.TaskStatus;
import com.rice.lfcdemo.enums.TimerStatus;
import com.rice.lfcdemo.exception.BusinessException;
import com.rice.lfcdemo.exception.ErrorCode;
import com.rice.lfcdemo.mapper.TimerTaskMapper;
import com.rice.lfcdemo.mapper.XtimerMapper;

import com.rice.lfcdemo.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ExecutorWorker {

    @Autowired
    private TimerTaskMapper timerTaskMapper;
    @Autowired
    private XtimerMapper timerMapper;

    public void work(String timerIDUnix) {
        List<Long> longs = TimerUtils.SplitTimerIDUnix(timerIDUnix);
        if (longs.size() != 2) {
            log.error("splitTimerIDUnix 错误, timerIDUnix:"+timerIDUnix);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"splitTimerIDUnix 错误, timerIDUnix:"+timerIDUnix);
        }
        Long timerId = longs.get(0);
        Long runtimer = longs.get(1);
        TaskModel taskModel = timerTaskMapper.getTasksByTimerIdUnix(timerId, runtimer);
        if(taskModel.getStatus() != TaskStatus.NotRun.getStatus()){
            log.warn("重复执行任务： timerId"+timerId+",runtimer:"+runtimer);
            return;
        }
        executeAndPostProcess(taskModel, timerId, runtimer);

    }

    private void executeAndPostProcess(TaskModel taskModel, Long timerId, Long runtimer) {
        Xtimer timer = timerMapper.getTimerById(timerId);
        if (timer == null) {
            log.error("执行回调错误，找不到对应的Timer。 timerId"+timerId);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"执行回调错误，找不到对应的Timer。 timerId"+timerId);
        }
        if (timer.getStatus().equals(TimerStatus.Unable.getStatus())) {
            log.warn("Timer已经处于去激活状态。 timerId"+timerId);
            return;
        }
        long gapTime = Math.max(0, new Date().getTime() - taskModel.getRunTimer());
        taskModel.setCostTime(gapTime);

        ResponseEntity<String> res = null;

        try {
            res = executeTimerCallBack(timer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (res == null) {
            taskModel.setStatus(TaskStatus.Failed.getStatus());
            taskModel.setOutput("res is null");
        }else if (res.getStatusCode().is2xxSuccessful()){
            taskModel.setStatus(TaskStatus.Succeed.getStatus());
            taskModel.setOutput(res.toString());
        }else{
            taskModel.setStatus(TaskStatus.Failed.getStatus());
            taskModel.setOutput(res.toString());
        }
        timerTaskMapper.update(taskModel);

    }

    private ResponseEntity<String> executeTimerCallBack(Xtimer timer) {
        TimerDTO timerDTO = Xtimer.objToVo(timer);
        NotifyHTTPParam notifyHTTPParam = timerDTO.getNotifyHTTPParam();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> res = null;
        switch (notifyHTTPParam.getMethod()){
            case "POST":
                res = restTemplate.postForEntity(notifyHTTPParam.getUrl(), timerDTO, String.class);
                break;
            default:
                log.error("不支持的httpMethod");
                break;
        }
        HttpStatusCode statusCode = res.getStatusCode();
        if (!statusCode.is2xxSuccessful()){
            log.error("http 回调失败："+ res);
        }
        return res;


    }
}

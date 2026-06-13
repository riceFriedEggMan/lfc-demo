package com.rice.msg.consumer;

import com.rice.lfcdemo.redis.ReentrantDistributeLock;
import com.rice.msg.consumer.poll.TimerMsgResendPollTask;
import com.rice.msg.enums.MsgStatus;
import com.rice.msg.mapper.MsgQueueTimerMapper;
import com.rice.msg.model.MsgQueueTimerModel;
import com.rice.msg.redis.TimerMsgCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TimerMsgConsumer {

    @Autowired
    private ReentrantDistributeLock reentrantDistributeLock;
    @Autowired
    private TimerMsgCache timerMsgCache;
    @Autowired
    private MsgQueueTimerMapper msgQueueTimerMapper;
    @Autowired
    private TimerMsgResendPollTask timerMsgResendPollTask;

    private boolean isLeader = false;

    private static final int LOCK_TIMER_RETRY_INTERVAL_SECONDS = 10;

    @Scheduled(fixedRate = 500)
    public void consume() throws InterruptedException {
        if (isLeader){
            consumeTimerMsgs();
        }else{
            log.info("定时消费者作为备用节点，等待成为主节点");
            Thread.sleep(LOCK_TIMER_RETRY_INTERVAL_SECONDS*1000);
            isLeader = tryBeLeader();
            if (isLeader){
                log.info("定时消费者从备用节点升级为主节点");
            }
        }
    }

    private boolean tryBeLeader() {
        String lockToken = System.currentTimeMillis()+Thread.currentThread().getName();
        boolean ok = reentrantDistributeLock.lockWithDog("TIMER_MSG_LEADER_CONSUMER_JAVA",
                lockToken, LOCK_TIMER_RETRY_INTERVAL_SECONDS);
        if (ok){
            return true;
        }else{
            log.warn("timer consumer get lock failed！");
            return false;
        }

    }

    private void consumeTimerMsgs() {
        List<String> times = timerMsgCache.getOnTimePointsFromCahce();
        if (times == null || times.size() == 0){
            return;
        }
        List<MsgQueueTimerModel> msgQueueTimerModels = msgQueueTimerMapper.getOnTimeMsgsList(MsgStatus.Pending.getStatus(), new Date().getTime());
        if (msgQueueTimerModels == null && msgQueueTimerModels.size() == 0){
            return;
        }

        List<String> msgIds = msgQueueTimerModels.stream().map(MsgQueueTimerModel::getMsgId)
                .collect(Collectors.toList());
        msgQueueTimerMapper.batchSetStatus(msgIds, MsgStatus.Processing.getStatus());

        for (MsgQueueTimerModel msgQueueTimerModel : msgQueueTimerModels){
            // 多线程异步处理
            timerMsgResendPollTask.asyncHandleMsg(msgQueueTimerModel.getReq());
        }


    }
}

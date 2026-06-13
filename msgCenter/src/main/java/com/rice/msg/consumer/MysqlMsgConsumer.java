package com.rice.msg.consumer;

import com.rice.lfcdemo.redis.ReentrantDistributeLock;
import com.rice.msg.constant.Constants;
import com.rice.msg.consumer.poll.MysqlMsgPollTask;
import com.rice.msg.entity.TMsgQueue;
import com.rice.msg.enums.MsgStatus;
import com.rice.msg.enums.PriorityEnum;
import com.rice.msg.mapper.TMsgQueueMapper;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.utils.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MysqlMsgConsumer {

    private static final int LOCK_RETRY_INTERVAL_SECONDS = 10;

    private Map<PriorityEnum, Boolean> isLeaderMap = new HashMap<>();

    @Resource
    private ReentrantDistributeLock reentrantDistributeLock;

    @Autowired
    private TMsgQueueMapper tMsgQueueMapper;

    @Autowired
    private MysqlMsgPollTask mysqlMsgPollTask;

    // 定时消费不同优先级的消息
    //@Scheduled(fixedRate = 1000)
    public void consume() {
        consumeMySQLMsgWithLeaderCheck(PriorityEnum.PRIORITY_LOW, 10);
        consumeMySQLMsgWithLeaderCheck(PriorityEnum.PRIORITY_LOW, 20);
        consumeMySQLMsgWithLeaderCheck(PriorityEnum.PRIORITY_LOW, 40);
        consumeMySQLMsgWithLeaderCheck(PriorityEnum.PRIORITY_LOW, 10);
    }

    // 采用主从架构，可由多台机器中的一台消费
    private void consumeMySQLMsgWithLeaderCheck(PriorityEnum priorityEnum, int batch) {
        if (isLeaderMap.get(priorityEnum) != null && isLeaderMap.get(priorityEnum)) {
            consumeMySQLMsg(priorityEnum, batch);
        } else {
            log.info(PriorityEnum.GetPriorityStr(priorityEnum.getPriority()) + "消费者作为备用节点，等待成功主节点 ");
            try {
                Thread.sleep(LOCK_RETRY_INTERVAL_SECONDS * 1000);
            } catch (InterruptedException e) {
                log.error("定时异常");
            }

            boolean isLeader = tryBeLeader(priorityEnum);
            if (isLeader) {
                log.info(priorityEnum.getPriority() + "优先级消费者从备用节点变成主节点");
                isLeaderMap.put(priorityEnum, isLeader);
            }

        }
    }

    private boolean tryBeLeader(PriorityEnum priorityEnum) {
        String token = System.currentTimeMillis() + Thread.currentThread().getName();
        String key = PriorityEnum.GetPriorityStr(priorityEnum.getPriority())+"_MSG_LEADER_CONSUMER_JAVA";
        boolean lock = reentrantDistributeLock.lockWithDog(key, token, LOCK_RETRY_INTERVAL_SECONDS);
        if (!lock) {
            log.info("加锁失败");
            return false;
        }
        return true;
    }

    // 多线程异步消费消息
    @Transactional
    public void consumeMySQLMsg(PriorityEnum priorityEnum, int batch) {
        String tableName = Constants.TableNamePre_MsgQueue + PriorityEnum.GetPriorityStr(priorityEnum.getPriority());

        List<TMsgQueue> msgQueueList = tMsgQueueMapper.getMsgByStatus(tableName, MsgStatus.Pending.getStatus(), batch);

        if (msgQueueList == null || msgQueueList.size() == 0){
            return;
        }

        List<String> msgIdList = msgQueueList.stream().map(TMsgQueue::getMsgId).collect(Collectors.toList());
        tMsgQueueMapper.batchSetStatus(tableName, msgIdList, MsgStatus.Processing.getStatus());

        for (TMsgQueue msgQueue : msgQueueList) {
            SendMsgReq sendMsgReq = new SendMsgReq();
            sendMsgReq.setMsgId(msgQueue.getMsgId());
            sendMsgReq.setPriority(priorityEnum.getPriority());
            sendMsgReq.setTo(msgQueue.getTo());
            sendMsgReq.setSubject(msgQueue.getSubject());
            sendMsgReq.setTemplateId(msgQueue.getTemplateId());

            Map<String, String> data = JSONUtil.parseMap(msgQueue.getTemplateData(), String.class, String.class);
            sendMsgReq.setTemplateData(data);
            mysqlMsgPollTask.asyncHandleMsg(sendMsgReq);
        }


    }


}
package com.rice.lfcdemo.redis;

import com.rice.lfcdemo.common.conf.SchedulerAppConf;
import com.rice.lfcdemo.entity.TaskModel;
import com.rice.lfcdemo.exception.BusinessException;
import com.rice.lfcdemo.exception.ErrorCode;
import com.rice.lfcdemo.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.stream.Task;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class TaskCache {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SchedulerAppConf schedulerAppConf;

    public boolean cacheTaskList(List<TaskModel> taskModels) {
        try {
            SessionCallback objectSessionCallback = new SessionCallback<>(){
                @Override
                public  Object execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    for (TaskModel taskModel : taskModels) {
                        Long runTimer = taskModel.getRunTimer();
                        String tableName = GetTableName(taskModel);
                        redisTemplate.opsForZSet().add(tableName,
                                TimerUtils.UnionTimerIDUnix(taskModel.getTimerId(), runTimer),
                                runTimer);

                    }
                    return operations.exec();
                }
            };
            redisTemplate.execute(objectSessionCallback);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String GetTableName(TaskModel taskModel) {
        int bucketsNumTemp = schedulerAppConf.getBucketsNum();
        Integer dynamicNum = (Integer) redisTemplate.opsForValue().get(TimerUtils.GetWorkerNumKey());
        if (dynamicNum == null) {
            dynamicNum = 0;
        }
        int bucketsNum = Math.max(bucketsNumTemp, dynamicNum);
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String timeStr = sdf.format(new Date(taskModel.getRunTimer()));
        long index = taskModel.getTimerId() % bucketsNum;
        return sb.append(timeStr).append("_").append(index).toString();
    }


    public List<TaskModel> getTasksFromCache(String minuteBucketKey, long tStart, long date) {
        ArrayList<TaskModel> taskModels = new ArrayList<>();

        Set<Object> timerIDUnixs = redisTemplate.opsForZSet().rangeByScore(minuteBucketKey, tStart, date);
        if (CollectionUtils.isEmpty(timerIDUnixs)){
            return null;
        }

        for (Object timerIDUnix : timerIDUnixs) {
            TaskModel taskModel = new TaskModel();
            String timerIDUnixStr = (String) timerIDUnix;
            List<Long> split = TimerUtils.SplitTimerIDUnix(timerIDUnixStr);
            if(split.size() != 2){
                log.error("splitTimerIDUnix 错误, timerIDUnix:"+timerIDUnix);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"splitTimerIDUnix 错误, timerIDUnix:"+timerIDUnix);
            }
            taskModel.setTimerId(split.get(0));
            taskModel.setRunTimer(split.get(1));
            taskModels.add(taskModel);
        }
        return taskModels;
    }
}

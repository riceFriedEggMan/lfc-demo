package com.rice.lfcdemo.redis;

import com.rice.lfcdemo.common.conf.SchedulerAppConf;
import com.rice.lfcdemo.entity.TaskModel;
import com.rice.lfcdemo.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        int bucketsNum = schedulerAppConf.getBucketsNum();
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String timeStr = sdf.format(new Date(taskModel.getRunTimer()));
        long index = taskModel.getTimerId() % bucketsNum;
        return sb.append(timeStr).append("_").append(index).toString();
    }
}

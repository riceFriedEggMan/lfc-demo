package com.rice.msg.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Component
@Service
public class TimerMsgCache {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String GetCacheKey(){
        return "Timer_Msgs";
    }
    public List<String> getOnTimePointsFromCahce() {
        try {
            List<String> result = executeScript(GetCacheKey(), 0l, System.currentTimeMillis() / 1000);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private List<String> executeScript(String key, long l1, long l2) {
        String script = "local elements = redis.call('ZRANGEBYSCORE', KEYS[1], ARGV[1], ARGV[2])" +
                "for i, elem in ipairs(elements) do "+
                "redis.call('ZREM', KEYS[1], elem)" +
                "end" +
                "return elements;";
        return redisTemplate.execute(
                new DefaultRedisScript<>(script, List.class),
                Collections.singletonList(key),
                l1,
                l2
        );
    }

    public void cacheSaveMsgTimePoint(Long sendTimestamp) {
        redisTemplate.opsForZSet().add(GetCacheKey(), sendTimestamp, sendTimestamp);
    }
}

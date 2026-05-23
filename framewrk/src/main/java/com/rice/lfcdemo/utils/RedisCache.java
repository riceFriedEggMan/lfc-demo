package com.rice.lfcdemo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SuppressWarnings(value = {"uncheck", "rawtype"})
@Component
public class RedisCache {

    @Autowired
    private RedisTemplate redisTemplate;


    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public boolean expire(final String key, final long timeout, final TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    public <T> long setCacheList(final String key, final List<T> list) {
        Long count = redisTemplate.opsForList().rightPushAll(key, list);
        return count == null ? 0 : count;
    }

    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public <T>BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> set) {
        BoundSetOperations<String, T> boundSetOperations = redisTemplate.boundSetOps(key);
        Iterator<T> iterator = set.iterator();
        while (iterator.hasNext()) {
            boundSetOperations.add(iterator.next());
        }
        return boundSetOperations;
    }

    public <T>Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }


}

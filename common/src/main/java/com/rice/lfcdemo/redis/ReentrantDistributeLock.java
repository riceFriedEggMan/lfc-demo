package com.rice.lfcdemo.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

@Component
@Slf4j
public class ReentrantDistributeLock {
    @Autowired
    private RedisBase redisBase;
    private RedisScript<Long> expireLockScript;

    public boolean lock(String key, String token, long expireSeconds) {
        Object res = redisBase.get(key);
        if (res != null && StringUtils.equals(res.toString(), token)) {
            return true;
        }

        boolean ok = redisBase.setnx(key, token, expireSeconds);
        if (!ok) {
            log.info("lock is acquired by others");
        }
        return ok;
    }

    public boolean lockWithDog(String key, String token, long expireSeconds) {
        Object res = redisBase.get(key);
        if (res != null && StringUtils.equals(res.toString(), token)) {
            return true;
        }
        boolean ok = redisBase.setnx(key, token, expireSeconds);
        if(!ok){
            log.info("lock is acquired by others");
            return false;
        }

        Timer dog = new Timer("dog");
        TimerTask timerTask = new TimerTask() {
            public void run() {
                expireLock(key, token, expireSeconds);
            }
        };
        dog.scheduleAtFixedRate(timerTask, 0, (expireSeconds / 3) * 1000);
        return ok;
    }

    private void expireLock(String key, String token, long expireSeconds) {
        Long execute = redisBase.executeLua(getExpireLockScript(), Arrays.asList(key), token, expireSeconds);
        if (execute.longValue() == 0) {
            log.info("延期{}失败:{}", key, execute);
        }else if (execute.longValue() == 1) {
            log.info("延期{}成功:{}", key, execute);
        }
    }

    public void unlock(String key, String token) {
        Long executeLua = redisBase.executeLua(getUnlockScript(), Arrays.asList(key), token, null);
        if (executeLua.longValue() == 1){
            log.info("unlock fail");
        }else if (executeLua.longValue() == 0){
            log.info("unlock success");
        }
    }

    private RedisScript<Long> getUnlockScript() {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] )\n" +
                "then\n" +
                "return redis.call('del', KEYS[1])\n" +
                "else\n" +
                "return 0\n" +
                "end";
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptText(script);
        return defaultRedisScript;
    }

    public RedisScript<Long> getExpireLockScript() {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "return redis.call('expire', KEYS[1], ARGV[2])\n" +
                "else\n" +
                "return 0\n" +
                "end";
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptText(script);
        return defaultRedisScript;
    }
}

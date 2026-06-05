package com.rice.lfcdemo.service.CleanCache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class CleanCacheJob {

    @Autowired
    private RedisTemplate redisTemplate;


    @Scheduled(cron = "0 * * * * *")
    public void cleanCache() {
        log.info("Clean cache job");
        try {
            Date now = new Date();
            Date beforeTime = DateUtils.addHours(now, -1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
            String pattern = sdf.format(beforeTime) + "*";

            int deleted = scanAndDelete(pattern, 200);
            log.info("缓存清理完成，共删除 {} 个键", deleted);
        } catch (Exception e) {
            log.error("缓存清理失败", e);
        }

    }

    private int scanAndDelete(String pattern, int batchSize) {
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        if (factory == null) {
            return 0;
        }
        int totalDeleted = 0;

        try (RedisConnection connection = factory.getConnection()){
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                    .match(pattern)
                    .count(batchSize)
                    .build());
            List<byte[]> batch = new ArrayList<>();

            while (cursor.hasNext()) {
                batch.add(cursor.next());

                if (batch.size() >= batchSize) {
                    connection.del(batch.toArray(new byte[0][]));
                    totalDeleted += batchSize;
                    log.debug("已删除{}个键", totalDeleted);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()){
                connection.del(batch.toArray(new byte[0][]));
                totalDeleted += batch.size();
            }
        }catch (Exception e){
            log.error("SCAN ERROR", e);
        }
        return totalDeleted;
    }

}

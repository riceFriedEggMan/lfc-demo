package com.rice.lfcdemo.service.migrator;

import com.rice.lfcdemo.common.conf.MigratorAppConf;
import com.rice.lfcdemo.entity.Xtimer;
import com.rice.lfcdemo.enums.TimerStatus;
import com.rice.lfcdemo.manage.MigratorManager;
import com.rice.lfcdemo.mapper.XtimerMapper;
import com.rice.lfcdemo.redis.ReentrantDistributeLock;
import com.rice.lfcdemo.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MigratorWorker {

    @Autowired
    private ReentrantDistributeLock reentrantDistributeLock;
    @Autowired
    private MigratorAppConf migratorAppConf;
    @Autowired
    private XtimerMapper xtimerMapper;
    @Autowired
    private MigratorManager migratorManager;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(fixedRate = 10 * 1000)
    public void work(){
        // 获取当前小时
        // 加锁，当前小时只给执行一次，加锁时长为一个小时，加锁键为当前小时
        // 获取锁后，执行迁移
        Date startHour = getStartHour(new Date());
        String value = TimerUtils.GetTokenStr();
        boolean ok = reentrantDistributeLock.lock(
                TimerUtils.GetMigratorLockKey(startHour),
                value,
                60L * migratorAppConf.getMigrateTryLockMinutes());
        if (!ok){
            log.warn("migrator get lock failed！"+TimerUtils.GetMigratorLockKey(startHour));
            return;
        }
        migrate();
    }

    private void migrate() {
        List<Xtimer> xtimerList = xtimerMapper.getXtimerByStatus(TimerStatus.Enable.getStatus());
        if (CollectionUtils.isEmpty(xtimerList)){
            log.info("xtimer list is empty");
            return;
        }
        // 根据配置的参数，动态调整桶大小
        int batchWorkersNum = migratorAppConf.getBatchWorkersNum();
        int workersNum = xtimerList.size() % batchWorkersNum;

        redisTemplate.opsForValue().set(TimerUtils.GetWorkerNumKey(), workersNum);

        for (Xtimer xtimer : xtimerList) {
            migratorManager.migrateTimer(xtimer);
        }

    }

    private Date getStartHour(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        try {
            return sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

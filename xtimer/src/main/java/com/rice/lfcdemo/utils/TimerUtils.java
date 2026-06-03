package com.rice.lfcdemo.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.quartz.CronExpression;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimerUtils {



    public static Date GetForwardTwoMigrateStepEnd(Date start, int diffMinutes){
        Date end = new Date(start.getTime() + 2L * diffMinutes * 60000);
        return end;
    }

    public static List<Long> GetCronNextBetween(CronExpression cronExpression, Date now, Date end){
        ArrayList<Long> times = new ArrayList<>();
        if (end.before(now)) {
            return times;
        }

        for (Date start = now; start.before(end);){
            Date next = cronExpression.getNextValidTimeAfter(start);
            times.add(next.getTime());
            start = next;
        }
        return times;
    }

    public static String UnionTimerIDUnix(long timerId, long unix){
        return new StringBuilder().append(timerId).append("_").append(unix).toString();
    }

    public static String GetTokenStr(){
        long currentTimeMillis = System.currentTimeMillis();
        String name = Thread.currentThread().getName();
        return name + currentTimeMillis;
    }

    public static String GetEnableLockKey(String app){
        return "enable_timer_lock_"+app;
    }

    public static String GetMigratorLockKey(Date startHour) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        String date = sdf.format(startHour);
        return "migrator_lock_" + date;
    }
}

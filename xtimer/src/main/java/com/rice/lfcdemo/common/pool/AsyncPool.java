package com.rice.lfcdemo.common.pool;

import com.rice.lfcdemo.common.conf.SchedulerAppConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
@EnableAsync
public class AsyncPool {

    @Autowired
    private SchedulerAppConf schedulerAppConf;

    @Bean(name = "schedulerPool")
    public Executor schedulerPoolExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(schedulerAppConf.getCorePoolSize());
        executor.setMaxPoolSize(schedulerAppConf.getMaxPoolSize());
        executor.setQueueCapacity(schedulerAppConf.getQueueCapacity());
        executor.setThreadNamePrefix(schedulerAppConf.getNamePrefix());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}

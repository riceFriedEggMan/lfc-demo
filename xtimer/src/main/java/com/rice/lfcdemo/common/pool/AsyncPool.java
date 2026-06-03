package com.rice.lfcdemo.common.pool;

import com.rice.lfcdemo.common.conf.SchedulerAppConf;
import com.rice.lfcdemo.common.conf.TriggerAppConf;
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
    @Autowired
    private TriggerAppConf triggerAppConf;

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

    @Bean("triggerPool")
    public Executor triggerPoolExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(triggerAppConf.getCorePoolSize());
        executor.setMaxPoolSize(triggerAppConf.getMaxPoolSize());
        executor.setQueueCapacity(triggerAppConf.getQueueCapacity());
        executor.setThreadNamePrefix(triggerAppConf.getNamePrefix());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}

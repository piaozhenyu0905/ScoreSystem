package com.system.assessment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);            // 核心线程数为5
        executor.setMaxPoolSize(10);            // 最大线程数为10
        executor.setQueueCapacity(25);          // 队列容量为25
        executor.setThreadNamePrefix("EmailExecutor-"); // 设置线程名前缀
        executor.setKeepAliveSeconds(60);       // 非核心线程闲置时间60秒后被回收
        executor.setWaitForTasksToCompleteOnShutdown(true); // 应用关闭时等待任务完成
        executor.setAwaitTerminationSeconds(30); // 最大等待时间为30秒
        executor.initialize();
        return executor;
    }
}

package com.dobby.rabbitmqtutorial.config;

import com.dobby.rabbitmqtutorial.service.Consumer;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @Author Dooby Kim
 * @Date 2023/4/5 10:20 下午
 * @Version 1.0
 */
@Configuration
// @EnableAsync
public class AsyncTaskConfig implements AsyncConfigurer {

    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 设置线程池的核心线程数
        threadPoolTaskExecutor.setCorePoolSize(5);
        // 设置线程池最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(10);
        // 设置缓冲队列的长度
        threadPoolTaskExecutor.setQueueCapacity(10);
        // 设置线程池关闭时，是否要等待所有线程结束后再关闭
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程池等待所有线程结束的时间
        threadPoolTaskExecutor.setAwaitTerminationSeconds(60);
        // 设置所有线程的前缀名称
        threadPoolTaskExecutor.setThreadNamePrefix("Rabbit-Async-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

}

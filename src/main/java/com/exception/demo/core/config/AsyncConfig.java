package com.exception.demo.core.config;

import static java.util.concurrent.Executors.newFixedThreadPool;

import brave.propagation.CurrentTraceContext;
import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextSnapshot;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

  @Bean(name = "taskExecutor")
  public Executor taskExecutor(CurrentTraceContext currentTraceContext) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("Async-");
    executor.initialize();

    return currentTraceContext.executor(executor);
  }

  @Bean("threadFactory")
  ThreadFactory threadFactory(CurrentTraceContext currentTraceContext) {
    ThreadFactory delegate = Executors.defaultThreadFactory();
    return runnable -> delegate.newThread(currentTraceContext.wrap(runnable));
  }

  @Bean(destroyMethod = "shutdown")
  ExecutorService tracedPool() {
    ExecutorService delegate = Executors.newFixedThreadPool(16);
    return ContextExecutorService.wrap(
        delegate, ContextSnapshot::captureAll);
  }
}

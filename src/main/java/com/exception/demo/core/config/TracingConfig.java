//package com.exception.demo.core.config;
//
//import brave.propagation.CurrentTraceContext;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadFactory;
//
//@Configuration
//class TracingConfig {
//  @Bean("threadFactory")
//  ThreadFactory tracingThreadFactory(CurrentTraceContext currentTraceContext) {
//    ThreadFactory delegate = Executors.defaultThreadFactory();
//    return runnable -> delegate.newThread(currentTraceContext.wrap(runnable));
//  }
//}

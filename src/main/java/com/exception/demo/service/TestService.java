package com.exception.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestService {

  @Async
  public void asyncMethod(int num, String des) throws InterruptedException {
    for (int i=0;i<num;i++){
      log.info("Log= {} i={}",i, des );
    }
    Thread.sleep(1000);
  }

  public void test(int num, String des) throws InterruptedException {
    for (int i=0;i<num;i++){
      log.info("Log= {}, i={}",i, des );
    }
    Thread.sleep(1000);
  }
}

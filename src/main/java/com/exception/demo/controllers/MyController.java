package com.exception.demo.controllers;

import com.exception.demo.core.exception1.BusinessException;
import com.exception.demo.core.response.ResponseMessage;
import com.exception.demo.core.response.ResponseMessageBuilder;
import com.exception.demo.service.TestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.concurrent.ThreadFactory;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MyController {

  private final TestService testService;
  private final ThreadFactory threadFactory;

    @PostMapping("/test")
    public ResponseMessage<String> test(String name) {
        if(!name.equals("morkraksa")){
          throw new BusinessException("",String.format("This name %s is incorrect!", name));
        }
        return new ResponseMessageBuilder<String>()
                .success()
                .build();
    }

  @PostMapping("/test1")
  public ResponseMessage<String> test1(@RequestBody @Valid TestDto request) {
    if(!request.getName().equals("morkraksa")){
      throw new BusinessException("",String.format("This name %s is incorrect!", request.getName()));
    }
    return new ResponseMessageBuilder<String>()
        .success()
        .build();
  }


  @Data
  public static class TestDto{
      @NotBlank(message = "This name is required, Please try again")
      private String name;
  }
}

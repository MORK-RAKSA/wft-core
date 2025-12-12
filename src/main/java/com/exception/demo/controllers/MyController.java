package com.exception.demo.controllers;

import com.exception.demo.core.exception.BusinessException;
import com.exception.demo.core.response.ResponseMessage;
import com.exception.demo.core.response.ResponseMessageBuilder;
import com.exception.demo.service.TestService;
import com.exception.demo.workflow.WorkflowRequestFacade;
import com.exception.demo.workflow.dto.request.WorkflowPayloadRequestDto;
import com.exception.demo.workflow.dto.response.SubmitWorkflowResult;
import com.exception.demo.workflow.dto.response.TaskResponseDetailDto;
import com.exception.demo.workflow.service.TaskApprovalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ThreadFactory;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MyController {

  private final TestService testService;
  private final ThreadFactory threadFactory;
  private final WorkflowRequestFacade facade;
  private final TaskApprovalService taskApprovalService;

    @PostMapping("/test")
    public ResponseMessage<SubmitWorkflowResult> test(@RequestBody WorkflowPayloadRequestDto requestDto) {
        log.info("Payload received: {}", requestDto);
        facade.handle(requestDto);
        return new ResponseMessageBuilder<SubmitWorkflowResult>()
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

    @GetMapping("/task-details")
    public ResponseEntity<TaskResponseDetailDto> getTaskDetails(
        @RequestParam Long id
    ) {
        TaskResponseDetailDto result = taskApprovalService.getTaskDetails(id);
        return ResponseEntity.ok(result);
    }


  @Data
  public static class TestDto{
      @NotBlank(message = "This name is required, Please try again")
      private String name;
  }
}

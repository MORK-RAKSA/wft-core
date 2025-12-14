package com.exception.demo.workflow.service;

import com.exception.demo.workflow.dto.request.ApproveOneRequestDto;
import com.exception.demo.workflow.dto.request.SubmitWorkflowRequestDto;
import com.exception.demo.workflow.dto.response.SubmitWorkflowResult;
import com.exception.demo.workflow.dto.response.TaskApprovalResponseDto;
import com.exception.demo.workflow.dto.response.TaskResponseDetailDto;

public interface TaskApprovalService {
    TaskResponseDetailDto getTaskDetails(Long taskId);
    TaskApprovalResponseDto approveOne(ApproveOneRequestDto requestDto);
    SubmitWorkflowResult submitRequest(SubmitWorkflowRequestDto dto);
}

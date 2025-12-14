package com.exception.demo.workflow.handler;

import com.exception.demo.workflow.annotation.WorkflowApprove;
import com.exception.demo.workflow.annotation.WorkflowRequestType;
import com.exception.demo.workflow.annotation.WorkflowTaskDetail;
import com.exception.demo.workflow.annotation.WorkflowTaskType;
import com.exception.demo.workflow.dto.MobAppPendingDetailResDto;
import com.exception.demo.workflow.dto.request.ApproveOneRequestDto;
import com.exception.demo.workflow.dto.request.FeaturePreparedData;
import com.exception.demo.workflow.dto.response.TaskApprovalResponseDto;
import com.exception.demo.workflow.enums.RequestType;
import com.exception.demo.workflow.enums.TaskType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WorkflowTaskType(TaskType.MOBAPP)
@RequiredArgsConstructor
public class MobAppHandler {


    @WorkflowApprove
    public TaskApprovalResponseDto approve(ApproveOneRequestDto dto) {
        return new TaskApprovalResponseDto("Test Approve Mobile App Task");
    }

    @WorkflowTaskDetail
    public MobAppPendingDetailResDto viewDetail(Long taskId) {
        return MobAppPendingDetailResDto.builder().accountNo("MOBAPP_UNBLOCK").build();
    }

    @WorkflowRequestType(RequestType.MOBAPP_UNBLOCK)
    public FeaturePreparedData unblock(Map<String, Object> payload) {
        String accountNo = (String) payload.get("account_no");

        return FeaturePreparedData.builder()
            .taskType(TaskType.MOBAPP)
            .requestType(RequestType.MOBAPP_UNBLOCK)
            .taskTitle("Unblock Mobile App User (General)")
            .taskDescription("Request unblock mobile app user")
            .metadata(Map.of("account_no", accountNo))
            .build();
    }

    @WorkflowRequestType(RequestType.MOBAPP_DE_REGISTER)
    public FeaturePreparedData deregister(Map<String, Object> payload) {
        String accountNo = (String) payload.get("account_no");

        return FeaturePreparedData.builder()
            .taskType(TaskType.MOBAPP)
            .requestType(RequestType.MOBAPP_DE_REGISTER)
            .taskTitle("Deregister Mobile App User")
            .taskDescription("Deregister Mobile App User")
            .metadata(Map.of("account_no", accountNo))
            .build();
    }

    @WorkflowRequestType(RequestType.MOBAPP_UNBIND)
    public FeaturePreparedData unbind(Map<String, Object> payload) {
        return null;
    }
}

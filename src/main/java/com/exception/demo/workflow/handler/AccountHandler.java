package com.exception.demo.workflow.handler;

import com.exception.demo.workflow.annotation.WorkflowRequestType;
import com.exception.demo.workflow.annotation.WorkflowTaskDetail;
import com.exception.demo.workflow.annotation.WorkflowTaskType;
import com.exception.demo.workflow.dto.MobAppPendingDetailResDto;
import com.exception.demo.workflow.dto.request.FeaturePreparedData;
import com.exception.demo.workflow.enums.RequestType;
import com.exception.demo.workflow.enums.TaskType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WorkflowTaskType(TaskType.ACC_BALANCE)
@RequiredArgsConstructor
public class AccountHandler {

    @WorkflowTaskDetail
    public MobAppPendingDetailResDto viewDetail(Long taskId) {
        return MobAppPendingDetailResDto.builder().accountNo("FREEZE_BALANCE").build();
    }

    @WorkflowRequestType(RequestType.FREEZE_BALANCE)
    public FeaturePreparedData unblock(Map<String, Object> payload) {
        String accountNo = (String) payload.get("account_no");

        return FeaturePreparedData.builder()
            .taskType(TaskType.ACC_BALANCE)
            .requestType(RequestType.FREEZE_BALANCE)
            .taskTitle("Unblock Mobile App User (General)")
            .taskDescription("Request unblock mobile app user")
            .metadata(Map.of("account_no", accountNo))
            .build();
    }
}

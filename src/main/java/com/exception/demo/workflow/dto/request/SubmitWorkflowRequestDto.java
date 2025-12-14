package com.exception.demo.workflow.dto.request;

import com.exception.demo.workflow.enums.RequestType;
import com.exception.demo.workflow.enums.TaskType;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmitWorkflowRequestDto {
    private String accountNo;
    private Integer workflowId;
    private String requestedBy;
    private String remarks;
    private TaskType taskType;
    private RequestType requestType;
    private String taskTitle;
    private String taskDescription;

    private Map<String, Object> metadata;
}

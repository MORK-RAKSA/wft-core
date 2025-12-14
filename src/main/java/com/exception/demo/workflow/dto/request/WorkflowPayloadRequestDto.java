package com.exception.demo.workflow.dto.request;

import com.exception.demo.workflow.enums.RequestType;
import com.exception.demo.workflow.enums.TaskType;
import java.util.Map;
import lombok.Data;

@Data
public class WorkflowPayloadRequestDto {
    private RequestType requestType;
    private TaskType taskType;
    private Integer workflowId;
    private String requestedBy;
    private String remarks;
    private Map<String, Object> payload;
}

package com.exception.demo.workflow.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmitWorkflowResult {
    private Long taskId;
}

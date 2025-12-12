package com.exception.demo.workflow.dto.response;

import com.exception.demo.model.Auditable;
import com.exception.demo.workflow.enums.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TaskResponseDetailDto extends Auditable<String> {
    private Long id;
    private String title;
    private String description;
    private String taskType;
    private String requestType;
    private String branchCode;
    private String remark;
    private Status status;
    private String workflow;
}

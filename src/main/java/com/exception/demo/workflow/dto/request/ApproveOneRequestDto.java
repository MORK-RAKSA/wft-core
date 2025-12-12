package com.exception.demo.workflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApproveOneRequestDto {
    @NotBlank(message = "Task ID is required!")
    private Long taskId;

    @NotBlank(message = "Approved By User is required!")
    private String approvedBy;

    @Size(max = 500, message = "Description must be at most 500 characters")
    @Pattern(regexp = "^$|.{3,}", message = "Description must be at least 3 characters if provided")
    private String remark;
}

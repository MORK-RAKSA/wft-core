package com.exception.demo.workflow.dto;

import com.exception.demo.workflow.dto.response.TaskResponseDetailDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class MobAppPendingDetailResDto extends TaskResponseDetailDto {
    private String accountNo;
}

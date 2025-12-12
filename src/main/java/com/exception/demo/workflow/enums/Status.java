package com.exception.demo.workflow.enums;

import lombok.Getter;

@Getter
public enum Status {
    PENDING,
    REJECTED,
    APPROVED,

    SUBMITTED,
    INACTIVE,
    ACTIVE,
    DELETED,
    SUSPENDED,
    COMPLETED,
    DONE;
}

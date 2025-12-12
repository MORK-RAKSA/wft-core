package com.exception.demo.workflow.enums;

import lombok.Getter;

@Getter
public enum TaskType {
    MOBAPP("Mobile App"),
    ACC_BALANCE("Account Balance");

    private final String desc;
    TaskType(String desc) {
        this.desc = desc;
    }
}

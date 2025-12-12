package com.exception.demo.workflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestType {
    MOBAPP_UNBLOCK("UNBLOCK"),
    MOBAPP_DE_REGISTER("DE REGISTER"),
    MOBAPP_UNBLOCK_LOWER_12("UNBLOCK LOWER 12"),
    MOBAPP_UNBIND("MOBAPP_UNBIND"),
    FREEZE_BALANCE("FREEZE_BALANCE");

    private final String desc;
}

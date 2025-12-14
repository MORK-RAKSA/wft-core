package com.exception.demo.workflow.constants;

public final class MessageCode {

    public static final String SYSTEM = "SYSTEM";
    public static final String TASK_DOES_NOT_EXIST = "No task found with ID '%d'.";
    public static final String TASK_NOT_ENOUGH_LINE_MANAGERS = "Not enough line managers to satisfy required approvals for level %s";
    public static final String TASK_ALREADY_APPROVED = "You already approved.";

    private MessageCode() {}
}

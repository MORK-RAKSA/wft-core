package com.exception.demo.core.emunz;

public enum MessageType {
    // spotless:off
    ERROR("ERROR"),
    WARN("WARN"),
    BIZ_LOGIC("BIZ_LOGIC"),
    INFO("INFO"),
    SUCCESS("SUCCESS");

    // spotless:on
    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}

package com.exception.demo.core.domain;

import com.exception.demo.core.emunz.MessageType;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record LocalData(
        String source,
        String errorCode,
        String message,
        String devErrorCode,
        String devMessage,
        MessageType messageType,
        String displayMode,
        String alertType,
        HttpStatus httpStatus,
        Object data) {}

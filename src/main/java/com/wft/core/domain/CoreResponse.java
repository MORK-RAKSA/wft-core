package com.wft.core.domain;

import com.wft.core.emunz.MessageType;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@Builder
public record CoreResponse(
        String source,
        String errorCode,
        String message,
        String devErrorCode,
        String devMessage,
        MessageType messageType,
        String displayMode,
        String alertType,
        HttpStatus httpStatus,
        Object data
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

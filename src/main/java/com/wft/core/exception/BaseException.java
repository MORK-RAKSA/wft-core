package com.wft.core.exception;

import com.wft.core.domain.CoreResponse;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final transient CoreResponse data;

    protected BaseException(CoreResponse data) {
        super(message(data.errorCode(), data.devMessage()));
        this.data = data;
    }

    protected BaseException(CoreResponse data, Throwable cause) {
        super(message(data.errorCode(), data.devMessage()), cause);
        this.data = data;
    }

    protected BaseException(String message) {
        this(CoreResponse.builder().message(message).build());
    }

    protected BaseException(String errorCode, String devMessage) {
        this(CoreResponse.builder().errorCode(errorCode).devMessage(message(errorCode, devMessage)).build());
    }

    protected BaseException(String errorCode, String devMessage, String source) {
        this(CoreResponse.builder()
                .errorCode(errorCode)
                .devMessage(message(errorCode,  devMessage))
                .source(source)
                .build());
    }

    protected BaseException(String errorCode, String devMessage, Throwable cause) {
        this(CoreResponse.builder().errorCode(errorCode).devMessage(message(errorCode, devMessage)).build(), cause);
    }

    private static String message(final String errorCode, final String devMessage) {
        return String.format("(%s): %s", errorCode, devMessage);
    }
}

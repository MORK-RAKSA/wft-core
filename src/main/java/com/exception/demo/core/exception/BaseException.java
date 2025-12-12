package com.exception.demo.core.exception;

import com.exception.demo.core.domain.LocalData;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final LocalData data;

    protected BaseException(LocalData data) {
        super(message(data.errorCode(), data.devMessage()));
        this.data = data;
    }

    protected BaseException(LocalData data, Throwable cause) {
        super(message(data.errorCode(), data.devMessage()), cause);
        this.data = data;
    }

    protected BaseException(String devMessage) {
        this(LocalData.builder().devMessage(devMessage).build());
    }

    protected BaseException(String devMessage,  Throwable cause) {
        super(devMessage, cause);
        this.data = LocalData.builder().devMessage(devMessage).build();
    }

    protected BaseException(String errorCode, String devMessage) {
        this(LocalData.builder().errorCode(errorCode).devMessage(devMessage).build());
    }

    protected BaseException(String errorCode, String devMessage, String source) {
        this(LocalData.builder()
                .errorCode(errorCode)
                .devMessage(devMessage)
                .source(source)
                .build());
    }

    protected BaseException(String errorCode, String devMessage, Throwable cause) {
        this(LocalData.builder().errorCode(errorCode).devMessage(devMessage).build(), cause);
    }

    private static String message(final String errorCode, final String devMessage) {
        return String.format("(%s): %s", errorCode, devMessage);
    }
}

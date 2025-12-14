package com.exception.demo.core.exception;


import com.exception.demo.core.domain.LocalData;

public class ValidationException extends BaseException {

    public ValidationException(final LocalData data) {
        super(data);
    }

    public ValidationException(final LocalData data, final Throwable cause) {
        super(data, cause);
    }

    public ValidationException(final String errorCode, final String devMessage) {
        super(errorCode, devMessage);
    }

    public ValidationException(final String errorCode, final String devMessage, final String source) {
        super(errorCode, devMessage, source);
    }

    public ValidationException(final String errorCode, final String devMessage, final Throwable cause) {
        super(errorCode, devMessage, cause);
    }
}

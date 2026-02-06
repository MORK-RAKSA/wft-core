package com.wft.core.exception;


import com.wft.core.domain.CoreResponse;

public class BusinessException extends BaseException {

    public BusinessException(final BaseException baseException) {
        this(baseException.getData(), baseException);
    }

    public BusinessException(final CoreResponse data) {
        super(data);
    }

    public BusinessException(final CoreResponse data, final Throwable cause) {
        super(data, cause);
    }

    public BusinessException(final String message) {
        super(message);
    }

    public BusinessException(final String errorCode, final String devMessage) {
        super(errorCode, devMessage);
    }

    public BusinessException(final String errorCode, final String devMessage, final String source) {
        super(errorCode, devMessage, source);
    }

    public BusinessException(final String errorCode, final String devMessage, final Throwable cause) {
        super(errorCode, devMessage, cause);
    }
}

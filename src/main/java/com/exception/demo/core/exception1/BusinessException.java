package com.exception.demo.core.exception1;


import com.exception.demo.core.domain.LocalData;

public class BusinessException extends BaseException {

    public BusinessException(final BaseException baseException) {
        this(baseException.getData(), baseException);
    }

    public BusinessException(final LocalData data) {
        super(data);
    }

    public BusinessException(final LocalData data, final Throwable cause) {
        super(data, cause);
    }

    public BusinessException(final String devMessage) {
        super(devMessage);
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

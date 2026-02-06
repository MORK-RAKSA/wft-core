package com.wft.core.exception;


import com.wft.core.domain.CoreResponse;

public class SystemException extends BaseException {

    public SystemException(final CoreResponse data) {
        super(data);
    }

    public SystemException(final CoreResponse data, final Throwable cause) {
        super(data, cause);
    }

    public SystemException(final String errorCode, final String devMessage) {
        super(errorCode, devMessage);
    }

    public SystemException(final String errorCode, final String devMessage, final String source) {
        super(errorCode, devMessage, source);
    }

    public SystemException(final String errorCode, final String devMessage, final Throwable cause) {
        super(errorCode, devMessage, cause);
    }
}

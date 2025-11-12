package com.exception.demo.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class ApiException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    private int code;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ApiException(Throwable cause) {
        super(cause);
    }
}

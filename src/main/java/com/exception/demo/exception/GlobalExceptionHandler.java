package com.exception.demo.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseMessage<Object> handleMethodNotAllowed(ApiException ex, HttpServletRequest request, HttpServletResponse response) {
        return ResponseMessage.error(
                getStatus(response, ex.getCode()),
                ex.getMessage(),
                request.getHeader("traceId"),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseMessage<Object> handleMethodNotAllowed(MethodNotAllowedException ex, HttpServletRequest request, HttpServletResponse response) {
        response.getStatus();
        return ResponseMessage.error(
                getStatus(response, ex.getStatusCode().value()),
                ex.getMessage(),
                request.getHeader("traceId"),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseMessage<Void> handleValidationException(MethodArgumentNotValidException ex, HttpServletResponse response, HttpServletRequest request) {

        // * take the first field error only
        String errorMessage = ex.getBindingResult().getFieldErrors().stream().findFirst()
                .map(FieldError::getDefaultMessage).orElse("Fields Required");

        return ResponseMessage.error(
                getStatus(response, ex.getStatusCode().value()),
                errorMessage,
                request.getHeader("traceId"),
                request.getRequestURI()

        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseMessage<Void> handleJsonParseException(HttpMessageNotReadableException ex, HttpServletResponse response, HttpServletRequest request) {
        String errorMessage;
        Throwable rootCause = ex.getMostSpecificCause();
        if (rootCause instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {

            Class<?> enumType = ife.getTargetType();
            String allowedValues = Arrays.stream(enumType.getEnumConstants()).map(Object::toString)
                    .collect(Collectors.joining(", "));

            String fieldName = ife.getPath().isEmpty() ? "unknownField" : ife.getPath().get(0).getFieldName();

            errorMessage = String.format("Field '%s' invalid type: '%s' (allowed: %s)", fieldName, ife.getValue(),
                    allowedValues);
        } else {
            errorMessage = "Request body is invalid or unreadable.";
        }

        return ResponseMessage.error(
                getStatus(response, 405),
                errorMessage,
                request.getHeader("traceId"),
                request.getRequestURI()
        );
    }

    private Integer getStatus(HttpServletResponse response, Integer code) {
        HttpStatus status = HttpStatus.resolve(code);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        response.setStatus(status.value());
        return status.value();
    }
}

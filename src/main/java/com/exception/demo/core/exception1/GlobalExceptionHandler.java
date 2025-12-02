package com.exception.demo.core.exception1;

import com.exception.demo.core.constant.ApiCodeConstant;
import com.exception.demo.core.constant.DevErrorConstant;
import com.exception.demo.core.domain.LocalData;
import com.exception.demo.core.response.ResponseMessage;
import com.exception.demo.core.response.ResponseMessageBuilder;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @Value("${spring.application.name}")
    private String applicationName;

    private final MessageSource messageSource;

    private int makeHttpStatus(BaseException ex, HttpStatus defaultStatus) {
        HttpStatus status = Optional.ofNullable(ex.getData().httpStatus()).orElse(defaultStatus);
        return status.value();
    }

    private String resolveMessage(BaseException ex, Locale locale) {
        LocalData data = ex.getData();

        if (StringUtils.hasText(data.message())) {
            return data.message();
        }

        if (StringUtils.hasText(data.errorCode())) {
            try {
                String localized = messageSource.getMessage(data.errorCode(), null, locale);
                if (StringUtils.hasText(localized)) {
                    return localized;
                }
            } catch (NoSuchMessageException ignored) {
                // * not implement yet
            }
        }

        if (StringUtils.hasText(data.devMessage())) {
            return data.devMessage();
        }

        return "";
    }

    private ResponseMessage<Void> makeResponse(BaseException ex, Locale locale) {
        return new ResponseMessageBuilder<Void>()
                .addSource(getSource())
                .fail(ex, resolveMessage(ex, locale))
                .addDevMessage(ex.getData().devMessage())
                .build();
    }

    private String getSource() {
        return (applicationName == null || applicationName.isBlank()) ? "UNKNOWN" : applicationName;
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseMessage<Void>> handleValidation(ValidationException ex, Locale locale) {
        return ResponseEntity.status(makeHttpStatus(ex, HttpStatus.BAD_REQUEST)).body(makeResponse(ex, locale));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseMessage<Void>> handleBaseException(BaseException ex, Locale locale) {
        return ResponseEntity.status(makeHttpStatus(ex, HttpStatus.INTERNAL_SERVER_ERROR))
                .body(makeResponse(ex, locale));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseMessage<Map<String, String>> handleInvalidArguments(MethodArgumentNotValidException ex) {
        return new ResponseMessageBuilder<Map<String, String>>()
                .addSource(getSource())
                .fail(ApiCodeConstant.GENERAL_ERROR, DevErrorConstant.GENERAL_ERROR)
                .addDevMessage(ex.getBindingResult().getFieldErrors().stream()
                        .findFirst()
                        .map(FieldError::getDefaultMessage)
                        .orElse("Fields Required"))
                .build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseMessage<Void> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        if (ex.getSupportedMethods() == null) throw new AssertionError();
        return new ResponseMessageBuilder<Void>()
                .addSource(getSource())
                .fail("", DevErrorConstant.GENERAL_ERROR)
                .addDevMessage(String.format(
                        "Method '%s' not allowed. Supported: %s",
                        ex.getMethod(), String.join(", ", ex.getSupportedMethods())))
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseMessage<Void> handleInvalidJson(HttpMessageNotReadableException ex) {
        String errorMessage;
        Throwable rootCause = ex.getMostSpecificCause();
        if (rootCause instanceof InvalidFormatException ife
                && ife.getTargetType().isEnum()) {

            Class<?> enumType = ife.getTargetType();
            String allowedValues = Arrays.stream(enumType.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            String fieldName = ife.getPath().isEmpty()
                    ? "unknownField"
                    : ife.getPath().get(0).getFieldName();

            errorMessage = String.format(
                    "Field '%s' invalid type: '%s' (allowed: %s)", fieldName, ife.getValue(), allowedValues);
        } else {
            errorMessage = "Request body is invalid or unreadable.";
        }

        return new ResponseMessageBuilder<Void>()
                .addSource(getSource())
                .fail(ApiCodeConstant.GENERAL_ERROR, DevErrorConstant.GENERAL_ERROR)
                .addDevMessage(errorMessage)
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseMessage<Void> handleConstraintViolation(ConstraintViolationException ex) {
        String addDevMessage = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .collect(Collectors.joining("; "));

        return new ResponseMessageBuilder<Void>()
                .addSource(getSource())
                .fail("", DevErrorConstant.GENERAL_ERROR)
                .addDevMessage(addDevMessage)
                .build();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseMessage<Void> handleNoResource(NoResourceFoundException ex) {
        return new ResponseMessageBuilder<Void>()
                .addSource(getSource())
                .fail(ApiCodeConstant.GENERAL_ERROR, DevErrorConstant.GENERAL_ERROR)
                .addDevMessage(ex.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseMessage<Void> handleException(Exception ex) {
        log.error("Unexpected error:", ex);

        return new ResponseMessageBuilder<Void>()
                .addSource(getSource())
                .fail(ApiCodeConstant.GENERAL_ERROR, DevErrorConstant.GENERAL_ERROR)
                .addDevMessage(ex.getMessage())
                .build();
    }
}

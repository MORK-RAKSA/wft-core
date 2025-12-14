package com.exception.demo.core.response;

import com.exception.demo.core.constant.ApiCodeConstant;
import com.exception.demo.core.constant.AppConstant;
import com.exception.demo.core.constant.DevErrorConstant;
import com.exception.demo.core.domain.LocalData;
import com.exception.demo.core.emunz.MessageType;
import com.exception.demo.core.exception.BaseException;
import java.util.Optional;
import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

public class ResponseMessageBuilder<T> {
    private final ResponseMessage.ResponseMessageBuilder<T> messageBuilder = ResponseMessage.builder();

    public ResponseMessageBuilder<T> success() {
        messageBuilder
                .message(MessageType.SUCCESS.toString())
                .messageType(MessageType.SUCCESS)
                .errorCode(ApiCodeConstant.SUCCESS)
                .devErrorCode(ApiCodeConstant.SUCCESS)
                .devMessage(DevErrorConstant.DEV_MESSAGE_SUCESS)
                .displayMode(AppConstant.TOAST)
                .alertType(AppConstant.TOAST)
                .status(HttpStatus.OK)
                .build();

        return this;
    }

    public ResponseMessageBuilder<T> success(@NonNull final String successCode) {
        return success().addErrorCode(successCode, null);
    }

    public ResponseMessageBuilder<T> fail(@NonNull final BaseException ex) {
        return fail(ex, ex.getMessage());
    }

    @SuppressWarnings("unchecked")
    public ResponseMessageBuilder<T> fail(@NonNull final BaseException ex, @NonNull final String message) {
        LocalData data = ex.getData();
        messageBuilder
                .message(Optional.ofNullable(data.message())
                        .filter(StringUtils::hasText)
                        .or(() -> Optional.of(message).filter(StringUtils::hasText))
                        .orElse(MessageType.ERROR.toString()))
                .messageType(Optional.ofNullable(data.messageType()).orElse(MessageType.ERROR))
                .errorCode(Optional.ofNullable(data.errorCode()).orElse(""))
                .devErrorCode(Optional.ofNullable(data.devErrorCode()).orElse("000030"))
                .devMessage(Optional.ofNullable(data.devMessage())
                        .filter(StringUtils::hasText)
                        .or(() -> Optional.of(message).filter(StringUtils::hasText))
                        .orElse(MessageType.ERROR.toString()))
                .displayMode(Optional.ofNullable(data.displayMode()).orElse(AppConstant.TOAST))
                .alertType(Optional.ofNullable(data.alertType()).orElse(AppConstant.TOAST))
                .data((T) data.data())
                .status(Optional.ofNullable(data.httpStatus()).orElse(HttpStatus.BAD_REQUEST))
                .build();

        return this;
    }

    public ResponseMessageBuilder<T> fail(@NonNull final String errorCode, @NonNull final String message) {
        messageBuilder
                .message(message)
                .messageType(MessageType.ERROR)
                .errorCode(errorCode)
                .devErrorCode(errorCode)
                .devMessage(message)
                .displayMode(AppConstant.TOAST)
                .alertType(AppConstant.TOAST)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return this;
    }

    public ResponseMessageBuilder<T> addSource(final String source) {
        messageBuilder.source(source);
        return this;
    }

    public ResponseMessageBuilder<T> addMessage(final String message) {
        messageBuilder.message(message);
        return this;
    }

    public ResponseMessageBuilder<T> addHttpStatus(final HttpStatus status) {
        messageBuilder.status(status);
        return this;
    }

    public ResponseMessageBuilder<T> addErrorCode(final String code, final String message) {
        messageBuilder.errorCode(code);
        messageBuilder.message(message);
        return this;
    }

    public ResponseMessageBuilder<T> addData(final T data) {
        messageBuilder.data(data);
        return this;
    }

    public ResponseMessageBuilder<T> addDevMessage(final String devMessage) {
        messageBuilder.devMessage(devMessage);
        return this;
    }

    public final ResponseMessage<T> build() {
        messageBuilder.traceId(MDC.get("traceId"));
        return messageBuilder.build();
    }
}

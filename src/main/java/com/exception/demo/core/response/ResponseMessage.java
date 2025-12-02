package com.exception.demo.core.response;

import com.exception.demo.core.emunz.MessageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage<T> implements Serializable {

    @Serial
    @JsonIgnore
    private static final long serialVersionUID = -6627775308255795557L;

    private String traceId;

    private String message;

    private String errorCode;

    private MessageType messageType;

    private String source;

    private String devErrorCode;

    private String devMessage;

    private String displayMode;

    private String alertType;

    private transient T data;

    private HttpStatus status;
}

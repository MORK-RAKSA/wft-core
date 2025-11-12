package com.exception.demo.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseMessage<T> {

    private String message;
    private Integer code;
    private T data;
    private String traceId;
    private String path;

    public static <T> ResponseMessage<T> success(String message) {
        return ResponseMessage.<T>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    public static <T> ResponseMessage<T> success(String message, T data) {
        return ResponseMessage.<T>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseMessage<T> error(Integer code, String message, String traceId, String path) {
        return ResponseMessage.<T>builder()
                .code(code)
                .message(message)
                .traceId(traceId)
                .path(path)
                .build();
    }
}
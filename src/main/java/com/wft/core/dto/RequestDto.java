package com.wft.core.dto;

import lombok.Data;

@Data
public class RequestDto {
    private String name;
    private String fullName;
    private String email;
    private String password;
}

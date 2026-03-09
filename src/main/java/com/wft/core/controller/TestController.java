package com.wft.core.controller;


import com.wft.core.dto.RequestDto;
import com.wft.core.response.ResponseMessage;
import com.wft.core.response.ResponseMessageBuilder;
import com.wft.core.services.annotations.SnakeModelAttribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @GetMapping(value = "/test")
    public ResponseMessage<RequestDto> test(@SnakeModelAttribute RequestDto requestDto){
        log.info("\n\n\t requestDto = {}", requestDto);
        return new ResponseMessageBuilder<RequestDto>().success().addData(requestDto).build();
    }
}

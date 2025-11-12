package com.exception.demo;

import com.exception.demo.exception.ApiException;
import com.exception.demo.exception.ResponseMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
    @PostMapping("/hello")
    public ResponseMessage<String> hello(String name) {
        if (name.equals("error")) {
            throw new ApiException(409, "error");
        }
        return ResponseMessage.success("Hello " + name);
    }
}

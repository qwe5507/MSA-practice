package com.example.userservice.controller;

import com.example.userservice.vo.Greeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {

    private final Greeting greeting;

    public UserController(final Greeting greeting) {
        this.greeting = greeting;
    }

    @GetMapping("/health_check")
    public String status() {
        return "It's Working in User Service.";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return greeting.getMessage();
    }
}

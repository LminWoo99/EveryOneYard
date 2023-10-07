package com.example.VideoChatting.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/ver")
    public String hello() {
        return "hello ec2 & docker ver4";
    }
}

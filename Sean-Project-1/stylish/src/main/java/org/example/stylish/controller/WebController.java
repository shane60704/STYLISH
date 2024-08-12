package org.example.stylish.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebController {
    @GetMapping("/")
    public String home() {
        return "Hello, My Server!";
    }
}

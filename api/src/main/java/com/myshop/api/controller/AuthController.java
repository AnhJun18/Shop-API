package com.myshop.api.controller;

import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth/page")
public class AuthController {

    @GetMapping("/get")
    public String in(){
        return  "hello";
    }
}

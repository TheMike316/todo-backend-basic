package com.example.todoapi.web.v1.controller;

import com.example.todoapi.service.AuthenticationService;
import com.example.todoapi.web.v1.model.JwtRequest;
import com.example.todoapi.web.v1.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;
    
    @PostMapping("/login")
    public UserDto authenticate(@RequestBody JwtRequest request) {
        return service.authenticate(request.getUsername(), request.getPassword());
    }
}

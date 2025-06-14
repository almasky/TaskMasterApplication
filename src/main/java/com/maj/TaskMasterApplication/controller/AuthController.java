package com.maj.TaskMasterApplication.controller;

import com.maj.TaskMasterApplication.dto.AuthResponseDto;
import com.maj.TaskMasterApplication.dto.LoginRequestDto;
import com.maj.TaskMasterApplication.dto.SignUpRequestDto;
import com.maj.TaskMasterApplication.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        // The service will handle username/email existence checks
        AuthResponseDto authResponse = userService.registerUser(signUpRequestDto);
        // Consider returning HttpStatus.CREATED (201) for successful registration
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        AuthResponseDto authResponse = userService.loginUser(loginRequestDto);
        return ResponseEntity.ok(authResponse);
    }
}
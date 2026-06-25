package com.example.todo_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.todo_app.dto.AuthenticationRequest;
import com.example.todo_app.dto.AuthenticationResponse;
import com.example.todo_app.service.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    // Định nghĩa API Đăng nhập dạng POST
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        // Nhận dữ liệu từ request -> chuyển giao cho Service xử lý -> Trả về Token
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
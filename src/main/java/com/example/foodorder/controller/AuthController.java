package com.example.foodorder.controller;

import com.example.foodorder.dto.AuthResponse;
import com.example.foodorder.dto.SignInRequest;
import com.example.foodorder.dto.SignUpRequest;

import com.example.foodorder.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(
            @RequestBody SignUpRequest request
    ) {

        return ResponseEntity.ok(
                authService.signUp(request)
        );
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(
            @RequestBody SignInRequest request
    ) {

        return ResponseEntity.ok(
                authService.signIn(request)
        );
    }
}
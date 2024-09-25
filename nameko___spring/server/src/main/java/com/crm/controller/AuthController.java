package com.crm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crm.dto.AuthResponse;
import com.crm.model.RegisterRequest;
import com.crm.model.SigninRequest;
import com.crm.service.AuthService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> signin(@RequestBody SigninRequest request) {
        return authService.signinUser(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout() {
        return authService.logoutUser();
    }
}



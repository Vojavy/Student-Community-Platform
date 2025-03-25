package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.dto.AuthRequest;
import com.vojavy.AlmAgoraHub.dto.AuthResponse;
import com.vojavy.AlmAgoraHub.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok("Регистрация прошла успешно. Проверьте почту для подтверждения.");
    }

    // GET /api/auth/verify?email=...&code=...
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String email, @RequestParam String code) {
        boolean result = authService.verifyUser(email, code);
        if (result) {
            return ResponseEntity.ok("Учетная запись подтверждена!");
        } else {
            return ResponseEntity.badRequest().body("Неверные данные или код");
        }
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

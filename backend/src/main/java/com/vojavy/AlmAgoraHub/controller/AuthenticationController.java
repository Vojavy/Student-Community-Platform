package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.config.JwtAuthFilter;
import com.vojavy.AlmAgoraHub.dto.LoginUserDto;
import com.vojavy.AlmAgoraHub.dto.RegisterUserDto;
import com.vojavy.AlmAgoraHub.dto.VerifyUserDto;
import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.responses.LoginResponse;
import com.vojavy.AlmAgoraHub.service.AuthenticationService;
import com.vojavy.AlmAgoraHub.service.JwtService;
import com.vojavy.AlmAgoraHub.service.UserService;
import com.vojavy.AlmAgoraHub.service.UserTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.annotation.AccessType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserTokenService userTokenService;
    private final UserService userService;

    public AuthenticationController(
            JwtService jwtService,
            AuthenticationService authenticationService,
            UserTokenService userTokenService,
            UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userTokenService = userTokenService;
        this.userService = userService;
    }

    //TODO возвращать только ок
    @PostMapping("/signup")
    public ResponseEntity<?> register(
            @RequestBody RegisterUserDto registerUserDto,
            HttpServletRequest request
    ) {
        String baseUrl = request.getHeader("Origin");
        User registeredUser = authenticationService.signupLocal(registerUserDto, baseUrl);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDto loginUserDto) {
        System.out.println(loginUserDto.getEmail());
        System.out.println(loginUserDto.getPassword());
        User authenticatedUser = authenticationService.authenticateLocal(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        long expiresAt = jwtService.getExpirationTime();

        Instant expirationInstant = Instant.now().plusMillis(expiresAt);
        userTokenService.saveToken(authenticatedUser, jwtToken, "auth", expirationInstant);

        LoginResponse loginResponse = new LoginResponse(jwtToken, expiresAt, "auth");
        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try{
            authenticationService.verifyUserLocal(verifyUserDto);
            return ResponseEntity.ok(new VerifyUserDto());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(
            @RequestParam String email,
            HttpServletRequest request){
        try {
            String baseUrl = request.getHeader("Origin");
            authenticationService.resendVerificationEmailLocal(email, baseUrl);
            return ResponseEntity.ok("resend verification email");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuthentication(@AuthenticationPrincipal UserDetails user) {
        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            response.put("authenticated", true);
            response.put("username", user.getUsername());
            return ResponseEntity.ok(response);
        } else {
            response.put("authenticated", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/check/uni")
    public ResponseEntity<Map<String, Object>> checkUniversityToken(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Map<String, Object> response = new HashMap<>();

        if (userDetails == null) {
            response.put("hasValidToken", false);
            response.put("ticket", null);
            return ResponseEntity.status(401).body(response);
        }

        try {
            String email = userDetails.getUsername();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            return userTokenService.getUniversityToken(user.getId())
                    .map(token -> {
                        boolean valid = token.getExpiration().isAfter(Instant.now());
                        response.put("hasValidToken", valid);
                        response.put("ticket", token.getToken());
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        response.put("hasValidToken", false);
                        response.put("ticket", null);
                        return ResponseEntity.ok(response);
                    });

        } catch (Exception e) {
            response.put("hasValidToken", false);
            response.put("ticket", null);
            return ResponseEntity.badRequest().body(response);
        }
    }


    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authenticationService.logoutByToken(token);
            return ResponseEntity.ok("Token invalidated");
        } else {
            return ResponseEntity.status(400).body("Missing or invalid Authorization header");
        }
    }
}

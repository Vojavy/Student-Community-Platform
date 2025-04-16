package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.config.JwtAuthFilter;
import com.vojavy.AlmAgoraHub.dto.LoginUserDto;
import com.vojavy.AlmAgoraHub.dto.RegisterUserDto;
import com.vojavy.AlmAgoraHub.dto.VerifyUserDto;
import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.responses.LoginResponse;
import com.vojavy.AlmAgoraHub.service.AuthenticationService;
import com.vojavy.AlmAgoraHub.service.JwtService;
import com.vojavy.AlmAgoraHub.service.UserTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserTokenService userTokenService;

    public AuthenticationController(
            JwtService jwtService,
            AuthenticationService authenticationService,
            UserTokenService userTokenService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userTokenService = userTokenService;
    }

    //TODO возвращать только ок
    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeregUser = authenticationService.signupLocal(registerUserDto);
        return ResponseEntity.ok(registeregUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDto loginUserDto) {
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
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email){
        try {
            authenticationService.resendVerificationEmailLocal(email);
            return ResponseEntity.ok("resend verification email");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

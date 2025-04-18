package com.vojavy.AlmAgoraHub.service;

import com.vojavy.AlmAgoraHub.dto.requests.LoginUserRequest;
import com.vojavy.AlmAgoraHub.dto.requests.RegisterUserRequest;
import com.vojavy.AlmAgoraHub.dto.VerifyUserDto;
import com.vojavy.AlmAgoraHub.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserTokenService userTokenService;

    public AuthenticationService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            EmailService emailService,
            UserTokenService userTokenService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userTokenService = userTokenService;
    }

    public User signupLocal(RegisterUserRequest input, String baseUrl) {
        User user = new User();
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setAuthProvider("local");
        user.setActive(false);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpires(LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.UTC));
        user.setProviderId(0);

        sendVerificationEmailLocal(user, baseUrl);
        return userService.save(user);
    }

    public User authenticateLocal(LoginUserRequest input) {
        User user = userService.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new AuthenticationException("Account not verified. Please verify your account") {};
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword())
        );

        return user;
    }

    public void verifyUserLocal(VerifyUserDto input) {
        Optional<User> optionalUser = userService.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getVerificationExpires().isBefore(LocalDateTime.now().toInstant(ZoneOffset.UTC))) {
                throw new RuntimeException("Verification expired");
            }

            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setActive(true);
                user.setVerificationCode(null);
                user.setVerificationExpires(null);
                userService.update(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationEmailLocal(String email, String baseUrl) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.isActive())
                throw new RuntimeException("User is active");

            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpires(LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.UTC));
            sendVerificationEmailLocal(user, baseUrl);
            userService.update(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void sendVerificationEmailLocal(User user, String baseUrl) {
        String subject = "Account Verification for AlmAgoraHub Students Platform";
        String verificationCode = user.getVerificationCode();

        String link = String.format("%s/verify?email=%s&code=%s", baseUrl, user.getEmail(), verificationCode);

        String htmlMessage = """
        <html>
        <head><style> /* стиль как у тебя */ </style></head>
        <body>
            <div class="container">
                <h2>Welcome to AlmAgoraHub 👋</h2>
                <p>To complete your registration, use this code:</p>
                <div class="code">%s</div>
                <p>Or click the button below:</p>
                <a href="%s" class="verify-btn">Verify My Account</a>
                <p class="footer">
                    This code is valid for 15 minutes. If you didn’t request this, you can safely ignore this email.
                </p>
                <div class="footer">
                    &copy; AlmAgoraHub Students Platform<br>
                    Do not reply to this email.
                </div>
            </div>
        </body>
        </html>
        """.formatted(verificationCode, link);

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        int randomInt = new Random().nextInt(900000) + 100000;
        return String.valueOf(randomInt);
    }

    public void logoutByToken(String token) {
        if (token != null) {
            userTokenService.deleteToken(token);
        }
    }
}

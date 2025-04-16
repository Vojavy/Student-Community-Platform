package com.vojavy.AlmAgoraHub.service;

import com.vojavy.AlmAgoraHub.dto.LoginUserDto;
import com.vojavy.AlmAgoraHub.dto.RegisterUserDto;
import com.vojavy.AlmAgoraHub.dto.VerifyUserDto;
import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.model.UserToken;
import com.vojavy.AlmAgoraHub.repository.UserRepository;
import org.hibernate.boot.archive.scan.internal.ScanResultImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public User signupLocal(RegisterUserDto input){
        User user = new User();
        UserToken userToken = new UserToken();

        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setAuthProvider("local");
        user.setActive(false);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpires(LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.UTC));
        user.setAuthProvider("local");
        user.setProviderId(0);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        sendVerificationEmailLocal(user);

        return userRepository.save(user);
    }

    public User authenticateLocal(LoginUserDto input){
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.isActive()) {
            throw new AuthenticationException("Account not verified. Please verify your account") {};
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        return user;
    }

    public void verifyUserLocal(VerifyUserDto input){
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationExpires().isBefore(LocalDateTime.now().toInstant(ZoneOffset.UTC))) {
                throw new RuntimeException("Verification expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setActive(true);
                user.setVerificationCode(null);
                user.setVerificationExpires(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationEmailLocal(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isActive())
                throw new RuntimeException("User is active");
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpires(LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.UTC));
            sendVerificationEmailLocal(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void sendVerificationEmailLocal(User user) {
        String subject = "Account Verification for AlmAgoraHub Students Platform";
        String verificationCode = user.getVerificationCode();

        String htmlMessage = """
        <html>
        <head>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f4;
                    color: #333;
                    padding: 20px;
                }
                .container {
                    background-color: #fff;
                    padding: 30px;
                    border-radius: 8px;
                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                    max-width: 500px;
                    margin: auto;
                }
                .code {
                    font-size: 28px;
                    font-weight: bold;
                    color: #2c3e50;
                    letter-spacing: 4px;
                    background: #eef2f5;
                    padding: 10px 20px;
                    border-radius: 6px;
                    display: inline-block;
                    margin: 20px 0;
                }
                .footer {
                    font-size: 12px;
                    color: #888;
                    margin-top: 20px;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h2>Welcome to AlmAgoraHub 👋</h2>
                <p>To complete your registration, please verify your account using the code below:</p>
                <div class="code">%s</div>
                <p>This code is valid for 15 minutes. If you didn’t request this, you can safely ignore this email.</p>
                <div class="footer">
                    &copy; AlmAgoraHub Students Platform<br>
                    Do not reply to this email.
                </div>
            </div>
        </body>
        </html>
        """.formatted(verificationCode);

       try {
           emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int randomInt = random.nextInt(900000) + 100000;
        return String.valueOf(randomInt);
    }
}

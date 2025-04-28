package com.vojavy.AlmAgoraHub.service.authentication;

import com.vojavy.AlmAgoraHub.dto.requests.LoginUserRequest;
import com.vojavy.AlmAgoraHub.dto.requests.RegisterUserRequest;
import com.vojavy.AlmAgoraHub.dto.VerifyUserDto;
import com.vojavy.AlmAgoraHub.model.user.Role;
import com.vojavy.AlmAgoraHub.model.user.RoleType;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.user.UserDetailsExtended;
import com.vojavy.AlmAgoraHub.service.EmailService;
import com.vojavy.AlmAgoraHub.service.user.RoleService;
import com.vojavy.AlmAgoraHub.service.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserTokenService userTokenService;
    private final RoleService roleService;

    public AuthenticationService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            EmailService emailService,
            UserTokenService userTokenService,
            RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userTokenService = userTokenService;
        this.roleService = roleService;
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

        UserDetailsExtended emptyDetails = new UserDetailsExtended();
        emptyDetails.setBio("");
        emptyDetails.setInterests(List.of());
        emptyDetails.setBirthDate(null);
        emptyDetails.setLanguages(List.of());
        emptyDetails.setContacts(Map.of(
                "inst", "",
                "tg", "",
                "fb", "",
                "steam", "",
                "ln", "",
                "telephone", ""
        ));
        emptyDetails.setOther((new ArrayList<Map<String, String>>())); // пустой, фронт потом добавит
        emptyDetails.setLocation("");
        emptyDetails.setWebsite("");
        emptyDetails.setStatus("");
        emptyDetails.setSkills(List.of());

        user.setDetails(emptyDetails);

        user.setRoles(Set.of(roleService.getByName(RoleType.ROLE_UNVERIFIED.name())));

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
        User user = userService.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Instant now = Instant.now();
        if (user.getVerificationExpires().isBefore(now)) {
            throw new RuntimeException("Verification expired");
        }

        if (!input.getVerificationCode().equals(user.getVerificationCode())) {
            throw new RuntimeException("Invalid verification code");
        }

        user.setActive(true);
        user.setVerificationCode(null);
        user.setVerificationExpires(null);

        Role userRole = roleService.getByName(RoleType.ROLE_USER.name());
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userService.update(user);
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

package com.vojavy.AlmAgoraHub.service;

import com.vojavy.AlmAgoraHub.dto.AuthRequest;
import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.repository.UserRepository;
import com.vojavy.AlmAgoraHub.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expiration}")
    private long expiration;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       MailService mailService,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void registerUser(AuthRequest request) {
        // Проверяем, нет ли уже пользователя с таким email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email уже используется");
        }

        // Генерируем код верификации
        String verificationCode = generateVerificationCode();

        // Создаём сущность User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(false);
        user.setAuthProvider("local");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        // Доп. поля, если нужно (domain_id, provider_id = null, и т.д.)

        userRepository.save(user);

        // Отправляем письмо с кодом
        mailService.sendVerificationEmail(user.getEmail(), verificationCode);

        // Логика сохранения verificationCode — либо в user_tokens, либо в поле user
        // Если в user_tokens, нужно добавить запись:
        // userTokensRepository.save(...), с token_type='verify', verification_code=..., user_id=...
        // Или в поле user.setVerificationCode(...), если оно существует в сущности User
    }

    // Метод верификации
    public boolean verifyUser(String email, String code) {
        // Если храните code в user_tokens, нужно там искать
        // Здесь для примера допустим, что code хранится в user
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // сверяем code, если он хранится в user или в user_tokens
            // если ок, то
            user.setActive(true);
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public String login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Неверный email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Неверный пароль");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("Учетная запись не подтверждена");
        }

        // Генерируем JWT
        return jwtTokenProvider.generateToken(user.getEmail());
    }

    private String generateVerificationCode() {
        // Можно сделать UUID.substring(0,6) или Random 6 цифр
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

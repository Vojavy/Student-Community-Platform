package com.vojavy.AlmAgoraHub.service.user;

import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.user.UserDetailsExtended;
import com.vojavy.AlmAgoraHub.repository.UserRepository;
import com.vojavy.AlmAgoraHub.dto.requests.UpdateUserRequest;
import com.vojavy.AlmAgoraHub.service.authentication.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User update(User updatedUser) {
        return userRepository.save(updatedUser);
    }

    public void updateUserWithPasswordCheck(Long userId, UpdateUserRequest request, BCryptPasswordEncoder encoder) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password");
        }

        user.setEmail(request.getEmail());

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            user.setPassword(encoder.encode(request.getNewPassword()));
        }

        user.setActive(request.isActive());
        userRepository.save(user);
    }

    public Long extractUserId(String tokenHeader){
        if (tokenHeader == null) return null;
        String token = tokenHeader.replace("Bearer ", "");
        return jwtService.extractClaim(token, claims -> claims.get("id", Long.class));
    }

    public void updateUserDetails(Long userId, UserDetailsExtended details) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setDetails(details);
        userRepository.save(user);
    }
}
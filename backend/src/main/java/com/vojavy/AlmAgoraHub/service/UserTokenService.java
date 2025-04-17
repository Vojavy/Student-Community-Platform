package com.vojavy.AlmAgoraHub.service;

import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.model.UserToken;
import com.vojavy.AlmAgoraHub.repository.UserRepository;
import com.vojavy.AlmAgoraHub.repository.UserTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserTokenService {
    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;

    public UserTokenService(
            UserTokenRepository userTokenRepository,
            UserRepository userRepository) {
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
    }

    public void saveToken(User user, String token, String tokenType, Instant expiresAt) throws RuntimeException {
        userRepository.findByEmail(user.getEmail()).orElseThrow(()-> new RuntimeException("User not found"));

        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setToken(token);
        userToken.setTokenType(tokenType);
        userToken.setExpiration(expiresAt);
        userToken.setCreatedAt(Instant.now());

        userTokenRepository.save(userToken);
    }

    public void deleteToken(String token) {
        userTokenRepository.findByToken(token)
                .ifPresent(userTokenRepository::delete);
    }
}

package com.vojavy.AlmAgoraHub.service.authentication;

import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.user.UserToken;
import com.vojavy.AlmAgoraHub.repository.UserRepository;
import com.vojavy.AlmAgoraHub.repository.UserTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

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

    public UserToken saveUniversityToken(Long userId, String token, Instant expiration, String origin) {
        Optional<UserToken> existing = userTokenRepository
                .findFirstByUser_IdAndTokenType(userId, "uni");
        existing.ifPresent(userToken -> deleteToken(userToken.getToken()));
        UserToken userToken = new UserToken();
        userToken.setUserId(userId);
        userToken.setToken(token);
        userToken.setTokenType("uni");
        userToken.setTokenOrigin(origin);
        userToken.setExpiration(expiration);
        userToken.setCreatedAt(Instant.now());
        return userTokenRepository.save(userToken);
    }

    public Optional<UserToken> getUniversityToken(Long userId) {
        return userTokenRepository.findFirstByUser_IdAndTokenType(userId, "uni");
    }

    public void deleteUniToken(Long userId) {
        userTokenRepository.findFirstByUser_IdAndTokenType(userId, "uni")
                .ifPresent(userTokenRepository::delete);
    }

    public Optional<UserToken> getUserTokenByToken(String token) {
        return userTokenRepository.findByToken(token);
    }
}

package com.vojavy.AlmAgoraHub.repository;

import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    List<UserToken> findUserTokensByUser(User user);
    List<UserToken> findUserTokensByToken(String token);
    Optional<UserToken> findByToken(String token);
    void delete(UserToken token);

    void deleteByUser(User user);
}

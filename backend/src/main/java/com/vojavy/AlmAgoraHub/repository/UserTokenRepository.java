package com.vojavy.AlmAgoraHub.repository;

import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    List<UserToken> findUserTokensByUser(User user);
    List<UserToken> findUserTokensByToken(String token);

    void deleteByUser(User user);
}

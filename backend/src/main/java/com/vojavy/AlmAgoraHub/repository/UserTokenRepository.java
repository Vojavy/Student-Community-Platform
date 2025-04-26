package com.vojavy.AlmAgoraHub.repository;

import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.user.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    List<UserToken> findUserTokensByUser(User user);
    Optional<UserToken> findFirstByUser_IdAndTokenType(Long userId, String tokenType);
    @Query("SELECT t FROM UserToken t WHERE t.token = :token")
    Optional<UserToken> findByToken(@Param("token") String token);
    void delete(UserToken token);
}

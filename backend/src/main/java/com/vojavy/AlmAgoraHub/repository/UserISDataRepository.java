package com.vojavy.AlmAgoraHub.repository;

import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.user.UserISData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserISDataRepository extends JpaRepository<UserISData, Long> {
    Optional<UserISData> findByUser(User user);
    Optional<UserISData> findByUserId(Long userId);
}

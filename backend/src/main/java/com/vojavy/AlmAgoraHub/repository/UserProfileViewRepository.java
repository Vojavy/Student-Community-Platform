package com.vojavy.AlmAgoraHub.repository;

import com.vojavy.AlmAgoraHub.model.UserProfileView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileViewRepository extends JpaRepository<UserProfileView, Long> {
    Optional<UserProfileView> findByUserId(Long userId);
}

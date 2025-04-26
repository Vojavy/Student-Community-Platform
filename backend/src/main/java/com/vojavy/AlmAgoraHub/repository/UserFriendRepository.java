package com.vojavy.AlmAgoraHub.repository;

import com.vojavy.AlmAgoraHub.model.user.UserFriend;
import com.vojavy.AlmAgoraHub.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFriendRepository extends JpaRepository<UserFriend, Long> {

    Optional<UserFriend> findByUser1AndUser2(User user1, User user2);

    List<UserFriend> findByUser1AndStatus(User user, String status);

    List<UserFriend> findByUser2AndStatus(User user, String status);

    List<UserFriend> findByUser1OrUser2AndStatus(User user1, User user2, String status);

    void deleteByUser1AndUser2(User user1, User user2);
}

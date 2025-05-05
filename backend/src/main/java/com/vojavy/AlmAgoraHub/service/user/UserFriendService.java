package com.vojavy.AlmAgoraHub.service.user;

import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.user.UserFriend;
import com.vojavy.AlmAgoraHub.repository.UserFriendRepository;
import com.vojavy.AlmAgoraHub.service.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserFriendService {

    private final UserFriendRepository friendRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public UserFriendService(
            UserFriendRepository friendRepository,
            UserService userService,
            NotificationService notificationService
    ) {
        this.friendRepository   = friendRepository;
        this.userService        = userService;
        this.notificationService = notificationService;
    }

    public void sendFriendRequest(Long fromUserId, Long toUserId) {
        User from = userService.findById(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + fromUserId));
        User to   = userService.findById(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + toUserId));

        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself.");
        }
        if (friendRepository.findByUser1AndUser2(from, to).isPresent()) {
            throw new IllegalStateException("Friend request already exists.");
        }

        UserFriend req = new UserFriend();
        req.setUser1(from);
        req.setUser2(to);
        req.setStatus("pending");
        req.setHidden(false);
        req.setCreatedAt(Instant.now());
        friendRepository.save(req);

        notificationService.sendFriendRequestNotification(from, to);
    }

    public void acceptFriendRequest(Long requesterId, Long receiverId) {
        User reqUser = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + requesterId));
        User recv   = userService.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + receiverId));

        UserFriend req = friendRepository.findByUser1AndUser2(reqUser, recv)
                .orElseThrow(() -> new IllegalStateException("No pending friend request found"));
        if (!"pending".equals(req.getStatus())) {
            throw new IllegalStateException("Request already processed");
        }
        req.setStatus("approved");
        friendRepository.save(req);
    }

    public void deleteFriendship(Long userId1, Long userId2) {
        Optional<User> u1 = userService.findById(userId1);
        Optional<User> u2 = userService.findById(userId2);
        if (u1.isEmpty() || u2.isEmpty()) return;

        friendRepository.findByUser1AndUser2(u1.get(), u2.get())
                .ifPresent(friendRepository::delete);
        friendRepository.findByUser1AndUser2(u2.get(), u1.get())
                .ifPresent(friendRepository::delete);
    }

    public List<UserFriend> getApprovedRelationsForUser(Long userId) {
        User me = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return friendRepository
                .findByUser1OrUser2AndStatus(me, me, "approved");
    }

    public Optional<UserFriend> getRelation(Long userId1, Long userId2) {
        User u1 = userService.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId1));
        User u2 = userService.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId2));

        Optional<UserFriend> rel = friendRepository.findByUser1AndUser2(u1, u2);
        if (rel.isEmpty()) {
            rel = friendRepository.findByUser1AndUser2(u2, u1);
        }
        return rel;
    }

    public List<User> getIncomingRequests(Long userId) {
        User me = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return friendRepository.findByUser2AndStatus(me, "pending")
                .stream()
                .map(UserFriend::getUser1)
                .collect(Collectors.toList());
    }
}

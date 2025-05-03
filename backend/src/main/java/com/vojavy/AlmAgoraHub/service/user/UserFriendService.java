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
        this.friendRepository = friendRepository;
        this.userService = userService;
        this.notificationService  = notificationService;
    }

    public void sendFriendRequest(Long fromUserId, Long toUserId) {
        Optional<User> fromUserOpt = userService.findById(fromUserId);
        Optional<User> toUserOpt = userService.findById(toUserId);

        if (fromUserOpt.isEmpty() || toUserOpt.isEmpty()) {
            throw new IllegalArgumentException("User(s) not found");
        }

        User fromUser = fromUserOpt.get();
        User toUser = toUserOpt.get();

        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself.");
        }

        if (friendRepository.findByUser1AndUser2(fromUser, toUser).isPresent()) {
            throw new IllegalStateException("Friend request already exists.");
        }

        UserFriend friendRequest = new UserFriend();
        friendRequest.setUser1(fromUser);
        friendRequest.setUser2(toUser);
        friendRequest.setStatus("pending");
        friendRequest.setHidden(false);
        friendRequest.setCreatedAt(Instant.now());

        friendRepository.save(friendRequest);

        notificationService.sendFriendRequestNotification(fromUser, toUser);
    }

    public void acceptFriendRequest(Long requesterId, Long receiverId) {
        Optional<User> requesterOpt = userService.findById(requesterId);
        Optional<User> receiverOpt = userService.findById(receiverId);

        if (requesterOpt.isEmpty() || receiverOpt.isEmpty()) {
            throw new IllegalArgumentException("User(s) not found");
        }

        User requester = requesterOpt.get();
        User receiver = receiverOpt.get();

        UserFriend request = friendRepository.findByUser1AndUser2(requester, receiver)
                .orElseThrow(() -> new IllegalStateException("No pending friend request found"));

        if (!"pending".equals(request.getStatus())) {
            throw new IllegalStateException("Request already accepted or invalid");
        }

        request.setStatus("approved");
        friendRepository.save(request);
    }

    public void deleteFriendship(Long userId1, Long userId2) {
        Optional<User> user1Opt = userService.findById(userId1);
        Optional<User> user2Opt = userService.findById(userId2);

        if (user1Opt.isEmpty() || user2Opt.isEmpty()) {
            return;
        }

        User user1 = user1Opt.get();
        User user2 = user2Opt.get();

        friendRepository.findByUser1AndUser2(user1, user2).ifPresent(friendRepository::delete);
        friendRepository.findByUser1AndUser2(user2, user1).ifPresent(friendRepository::delete);
    }

    public List<User> getFriendsForUser(Long userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return List.of();

        User user = userOpt.get();
        List<UserFriend> relations = friendRepository.findByUser1OrUser2AndStatus(user, user, "approved");

        return relations.stream()
                .map(f -> f.getUser1().equals(user) ? f.getUser2() : f.getUser1())
                .collect(Collectors.toList());
    }

    public List<User> getPendingFriendRequests(Long userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return List.of();

        return friendRepository.findByUser2AndStatus(userOpt.get(), "pending")
                .stream()
                .map(UserFriend::getUser1)
                .collect(Collectors.toList());
    }

    public List<User> getIncomingRequests(Long userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return List.of();

        return friendRepository.findByUser2AndStatus(userOpt.get(), "pending")
                .stream()
                .map(UserFriend::getUser1)
                .collect(Collectors.toList());
    }
}

package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.dto.responses.FriendResponse;
import com.vojavy.AlmAgoraHub.dto.responses.UserProfileResponse;
import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.model.UserProfileView;
import com.vojavy.AlmAgoraHub.repository.UserProfileViewRepository;
import com.vojavy.AlmAgoraHub.service.JwtService;
import com.vojavy.AlmAgoraHub.service.UserFriendService;
import com.vojavy.AlmAgoraHub.service.UserProfileService;
import com.vojavy.AlmAgoraHub.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/friends")
public class UserFriendController {

    private final UserFriendService friendService;
    private final JwtService jwtService;
    private final UserProfileService userProfileservice;
    private final UserService userService;

    public UserFriendController(UserFriendService friendService,
                                JwtService jwtService,
                                UserProfileService userProfileservice,
                                UserService userService) {
        this.friendService = friendService;
        this.jwtService = jwtService;
        this.userProfileservice = userProfileservice;
        this.userService = userService;
    }

    @PostMapping("/{id}/request")
    public ResponseEntity<?> sendFriendRequest(@PathVariable Long id, @RequestHeader("Authorization") String tokenHeader) {
        Long senderId = userService.extractUserId(tokenHeader);
        friendService.sendFriendRequest(senderId, id);
        return ResponseEntity.ok("Friend request sent");
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id, @RequestHeader("Authorization") String tokenHeader) {
        Long receiverId = userService.extractUserId(tokenHeader);
        friendService.acceptFriendRequest(id, receiverId);
        return ResponseEntity.ok("Friend request approved");
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<?> declineRequest(@PathVariable Long id, @RequestHeader("Authorization") String tokenHeader) {
        Long receiverId = userService.extractUserId(tokenHeader);
        friendService.deleteFriendship(id, receiverId);
        return ResponseEntity.ok("Friend request declined");
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<FriendResponse>> getIncoming(@RequestHeader("Authorization") String tokenHeader) {
        Long me = userService.extractUserId(tokenHeader);
        List<User> incoming = friendService.getIncomingRequests(me);
        List<FriendResponse> dto = incoming.stream()
                .map(u -> mapFriend(u.getId(), "pending"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<FriendResponse>> getAllFriends(@RequestHeader("Authorization") String tokenHeader,
                                                              @RequestParam(required = false) Long id) {
        Long userId = id != null ? id : userService.extractUserId(tokenHeader);
        List<User> friends = friendService.getFriendsForUser(userId);

        List<FriendResponse> response = friends.stream()
                .map(friend -> mapFriend(friend.getId(), "approved"))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendResponse> getFriendById(@PathVariable Long id, @RequestHeader("Authorization") String tokenHeader) {
        Long selfId = userService.extractUserId(tokenHeader);
        List<User> friends = friendService.getFriendsForUser(selfId);

        boolean isFriend = friends.stream().anyMatch(f -> f.getId().equals(id));
        if (!isFriend) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(mapFriend(id, "approved"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFriend(@PathVariable Long id, @RequestHeader("Authorization") String tokenHeader) {
        Long selfId = userService.extractUserId(tokenHeader);
        friendService.deleteFriendship(selfId, id);
        return ResponseEntity.ok("Friend removed");
    }

    private FriendResponse mapFriend(Long friendId, String status) {
        Optional<UserProfileResponse> profile = userProfileservice.getProfileById(friendId);
        String name = profile.map(p -> {
            if (p.getJmeno() != null && p.getPrijmeni() != null) {
                return p.getJmeno() + " " + p.getPrijmeni();
            } else {
                return p.getEmail();
            }
        }).orElse("Unknown");

        FriendResponse dto = new FriendResponse();
        dto.setUserId(friendId);
        dto.setName(name);
        dto.setStatus(status);
        return dto;
    }
}

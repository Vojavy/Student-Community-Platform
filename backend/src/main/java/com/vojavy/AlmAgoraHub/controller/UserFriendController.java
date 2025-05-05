package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.dto.responses.FriendResponse;
import com.vojavy.AlmAgoraHub.dto.responses.UserProfileResponse;
import com.vojavy.AlmAgoraHub.model.user.UserFriend;
import com.vojavy.AlmAgoraHub.service.user.UserFriendService;
import com.vojavy.AlmAgoraHub.service.user.UserProfileService;
import com.vojavy.AlmAgoraHub.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/friends")
public class UserFriendController {

    private final UserFriendService   friendService;
    private final UserProfileService  profileService;
    private final UserService         userService;

    public UserFriendController(
            UserFriendService friendService,
            UserProfileService profileService,
            UserService userService
    ) {
        this.friendService  = friendService;
        this.profileService = profileService;
        this.userService    = userService;
    }

    @PostMapping("/{id}/request")
    public ResponseEntity<?> sendFriendRequest(
            @PathVariable Long id,
            @RequestHeader("Authorization") String tokenHeader
    ) {
        Long me = userService.extractUserId(tokenHeader);
        friendService.sendFriendRequest(me, id);
        return ResponseEntity.ok("Friend request sent");
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(
            @PathVariable Long id,
            @RequestHeader("Authorization") String tokenHeader
    ) {
        Long me = userService.extractUserId(tokenHeader);
        friendService.acceptFriendRequest(id, me);
        return ResponseEntity.ok("Friend request approved");
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<?> declineRequest(
            @PathVariable Long id,
            @RequestHeader("Authorization") String tokenHeader
    ) {
        Long me = userService.extractUserId(tokenHeader);
        friendService.deleteFriendship(id, me);
        return ResponseEntity.ok("Friend request declined");
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<FriendResponse>> getIncoming(
            @RequestHeader("Authorization") String tokenHeader
    ) {
        Long me = userService.extractUserId(tokenHeader);
        List<FriendResponse> dto = friendService.getIncomingRequests(me).stream()
                .map(u -> mapFriend(u.getId(), "pending"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<FriendResponse>> getAllFriends(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam(required = false) Long id
    ) {
        Long userId = (id != null) ? id : userService.extractUserId(tokenHeader);
        List<FriendResponse> dto = friendService.getApprovedRelationsForUser(userId).stream()
                .map(rel -> {
                    Long otherId = rel.getUser1().getId().equals(userId)
                            ? rel.getUser2().getId()
                            : rel.getUser1().getId();
                    return mapFriend(otherId, rel.getStatus());
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendResponse> getFriendById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String tokenHeader
    ) {
        Long me = userService.extractUserId(tokenHeader);
        return friendService.getRelation(me, id)
                .map(rel -> {
                    Long otherId = rel.getUser1().getId().equals(me)
                            ? rel.getUser2().getId()
                            : rel.getUser1().getId();
                    return mapFriend(otherId, rel.getStatus());
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFriend(
            @PathVariable Long id,
            @RequestHeader("Authorization") String tokenHeader
    ) {
        Long me = userService.extractUserId(tokenHeader);
        friendService.deleteFriendship(me, id);
        return ResponseEntity.ok("Friend removed");
    }

    private FriendResponse mapFriend(Long friendId, String status) {
        UserProfileResponse p = profileService.getProfileById(friendId)
                .orElseThrow();
        String name = (p.getJmeno() != null && p.getPrijmeni() != null)
                ? p.getJmeno() + " " + p.getPrijmeni()
                : p.getEmail();
        FriendResponse dto = new FriendResponse();
        dto.setUserId(friendId);
        dto.setName(name);
        dto.setStatus(status);
        return dto;
    }
}

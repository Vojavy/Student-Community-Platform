package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.dto.requests.CreateGroupRequest;
import com.vojavy.AlmAgoraHub.dto.requests.GroupPostRequest;
import com.vojavy.AlmAgoraHub.dto.requests.GroupSettingsRequest;
import com.vojavy.AlmAgoraHub.dto.responses.*;
import com.vojavy.AlmAgoraHub.model.group.GroupPost;
import com.vojavy.AlmAgoraHub.service.User.UserService;
import com.vojavy.AlmAgoraHub.service.group.GroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService      groupService;
    private final UserService       userService;

    public GroupController(
            GroupService groupService,
            UserService userService
    ) {
        this.groupService = groupService;
        this.userService  = userService;
    }

    //=====================================
    // Group management
    //=====================================

    @PostMapping
    public GroupResponse createGroup(
            @RequestBody CreateGroupRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long creatorId = userService.extractUserId(authHeader);
        return GroupResponse.fromEntity(
                groupService.createGroup(req, creatorId)
        );
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        groupService.deleteGroup(userId, groupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailResponse> getGroupDetails(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        return groupService.getGroupDetails(groupId, userId);
    }

    @PutMapping("/{groupId}/settings")
    public ResponseEntity<GroupResponse> updateSettings(
            @PathVariable Long groupId,
            @RequestBody GroupSettingsRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        return ResponseEntity.ok(
                GroupResponse.fromEntity(
                        groupService.updateGroupSettings(groupId, userId, req)
                )
        );
    }

    @GetMapping
    public Page<GroupResponse> browseGroups(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long domainId,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) List<String> topics,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size
    ) {
        return groupService.browseGroups(name, domainId, isPublic, topics, page, size);
    }

    //=====================================
    // Membership management
    //=====================================

    @PostMapping("/{groupId}/join")
    public GroupMembershipResponse joinGroup(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        return groupService.requestToJoinGroup(groupId, userId);
    }

    @DeleteMapping("/{groupId}/leave")
    public GroupMembershipResponse leaveGroup(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        return groupService.requestToLeaveGroup(groupId, userId);
    }

    @PostMapping("/{groupId}/invite")
    public GroupMembershipResponse inviteUser(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long targetUserId,
            @RequestParam String role
    ) {
        Long inviterId = userService.extractUserId(authHeader);
        return groupService.inviteUserToGroup(inviterId, groupId, targetUserId, role);
    }

    @PutMapping("/{groupId}/members/{targetUserId}/role")
    public GroupMembershipResponse changeMemberRole(
            @PathVariable Long groupId,
            @PathVariable Long targetUserId,
            @RequestParam String newRole,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long actorId = userService.extractUserId(authHeader);
        return groupService.changeUserRole(actorId, groupId, targetUserId, newRole);
    }

    @PutMapping("/{groupId}/members/{targetUserId}/status")
    public GroupMembershipResponse handleJoinRequest(
            @PathVariable Long groupId,
            @PathVariable Long targetUserId,
            @RequestParam boolean approve,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long managerId = userService.extractUserId(authHeader);
        return groupService.handleJoinRequest(managerId, groupId, targetUserId, approve);
    }

    @DeleteMapping("/{groupId}/members/{targetUserId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long managerId = userService.extractUserId(authHeader);
        groupService.removeMember(managerId, groupId, targetUserId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{groupId}/members/{userId}/ban")
    public ResponseEntity<Void> banMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long actorId = userService.extractUserId(authHeader);
        groupService.banMember(actorId, groupId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{groupId}/members/{userId}/unban")
    public ResponseEntity<Void> unbanMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long actorId = userService.extractUserId(authHeader);
        groupService.unbanMember(actorId, groupId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/members")
    public List<GroupMembershipResponse> listMembers(
            @PathVariable Long groupId,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long currentUserId = userService.extractUserId(authHeader);
        return groupService.getGroupMembers(groupId, currentUserId, status);
    }

    @GetMapping("/user")
    public Page<GroupResponse> listMyGroups(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size
    ) {
        Long userId = userService.extractUserId(authHeader);
        return groupService.getUserGroups(userId, page, size);
    }

    @GetMapping("/{groupId}/members/{userId}/status")
    public ResponseEntity<MembershipStatusResponse> getMembershipStatus(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                groupService.getMembershipStatus(userId, userId, groupId)
        );
    }

    @GetMapping("/{groupId}/members/status")
    public ResponseEntity<MembershipStatusResponse> getMyMembershipStatus(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        return ResponseEntity.ok(
                groupService.getMembershipStatus(userId, userId, groupId)
        );
    }

    //=====================================
    // Posts management
    //=====================================

    @GetMapping("/{groupId}/posts")
    public Page<GroupPostResponse> listPosts(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "20")   int size,
            @RequestParam(required = false)      String search,
            @RequestHeader("Authorization")     String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);

        Page<GroupPost> postsPage = groupService.getPostsForGroup(
                groupId, userId, page, size, search
        );

        List<GroupPostResponse> dtos = postsPage.stream()
                .map(p -> {
                    GroupPostResponse r = GroupPostResponse.fromEntity(p);
                    r.setAuthor( groupService.toUserSummary(p.getUser()) );
                    return r;
                })
                .toList();

        return new PageImpl<>(
                dtos,
                postsPage.getPageable(),
                postsPage.getTotalElements()
        );
    }

    @GetMapping("/{groupId}/posts/{postId}")
    public GroupPostResponse getPost(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        GroupPost post = groupService.getPost(groupId, postId, userId);
        return GroupPostResponse.fromEntity(post);
    }

    @PostMapping("/{groupId}/posts")
    public ResponseEntity<GroupPostResponse> createPost(
            @PathVariable Long groupId,
            @RequestBody GroupPostRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        GroupPost created = groupService.createPost(groupId, userId, req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(GroupPostResponse.fromEntity(created));
    }

    @PutMapping("/{groupId}/posts/{postId}")
    public GroupPostResponse updatePost(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @RequestBody GroupPostRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        GroupPost updated = groupService.updatePost(groupId, postId, userId, req);
        return GroupPostResponse.fromEntity(updated);
    }

    @DeleteMapping("/{groupId}/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        groupService.deletePost(groupId, postId, userId);
        return ResponseEntity.noContent().build();
    }
}

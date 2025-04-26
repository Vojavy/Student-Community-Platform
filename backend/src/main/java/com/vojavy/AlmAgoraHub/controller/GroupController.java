package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.dto.requests.CreateGroupRequest;
import com.vojavy.AlmAgoraHub.dto.requests.GroupSettingsRequest;
import com.vojavy.AlmAgoraHub.dto.responses.GroupDetailResponse;
import com.vojavy.AlmAgoraHub.dto.responses.GroupMembershipResponse;
import com.vojavy.AlmAgoraHub.dto.responses.GroupResponse;
import com.vojavy.AlmAgoraHub.dto.responses.MembershipStatusResponse;
import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.group.GroupMembership;
import com.vojavy.AlmAgoraHub.service.UserService;
import com.vojavy.AlmAgoraHub.service.group.GroupMembershipService;
import com.vojavy.AlmAgoraHub.service.group.GroupService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService            groupService;
    private final GroupMembershipService  membershipService;
    private final UserService             userService;

    public GroupController(
            GroupService           groupService,
            GroupMembershipService membershipService,
            UserService            userService
    ) {
        this.groupService    = groupService;
        this.membershipService = membershipService;
        this.userService     = userService;
    }

    @PostMapping
    public Group createGroup(
            @RequestBody CreateGroupRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long creatorId = userService.extractUserId(authHeader);
        return groupService.createGroup(req, creatorId);
    }

    @PostMapping("/{groupId}/join")
    public GroupMembershipResponse joinGroup(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        return groupService.requestToJoinGroup(groupId, userId);
    }

    @PostMapping("/{groupId}/invite")
    public GroupMembership invite(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long targetUserId,
            @RequestParam String role
    ) {
        Long inviterId = userService.extractUserId(authHeader);
        return membershipService.inviteUserToGroup(groupId, inviterId, targetUserId, role);
    }

    @PostMapping("/{groupId}/leave")
    public GroupMembershipResponse leaveGroup(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        return groupService.requestToLeaveGroup(groupId, userId);
    }

    @PutMapping("/{groupId}/members/{targetUserId}/role")
    public GroupMembershipResponse changeRole(
            @PathVariable Long groupId,
            @PathVariable Long targetUserId,
            @RequestParam String newRole,
            @RequestHeader("Authorization") String authHeader
            ) {
        Long userId = userService.extractUserId(authHeader);
        return groupService.changeUserRole(userId, groupId, targetUserId, newRole);
    }


    @PutMapping("/{groupId}/members/{targetUserId}/status")
    public GroupMembership handleRequest(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long targetUserId,
            @RequestParam boolean approve
    ) {
        Long actorId = userService.extractUserId(authHeader);
        membershipService.handleJoinRequest(groupId, actorId, targetUserId, approve);
        return membershipService
                .getMembershipInfo(groupId, targetUserId)
                .orElse(null);
    }

    @DeleteMapping("/{groupId}/members/{targetUserId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long targetUserId
    ) {
        Long actorId = userService.extractUserId(authHeader);
        membershipService.removeUserFromGroup(groupId, actorId, targetUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/members")
    public List<GroupMembershipResponse> listMembers(
            @PathVariable Long groupId,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String authHeader
    ) {
        // извлекаем текущего пользователя из JWT
        Long currentUserId = userService.extractUserId(authHeader);
        // вызываем новый сервисный метод
        return groupService.getGroupMembers(groupId, currentUserId, status);
    }

    @GetMapping("/user")
    public Page<GroupResponse> listMyGroups(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size
    ) {
        Long userId = userService.extractUserId(authHeader);
        return membershipService.getGroupsForUser(userId, page, size);
    }

    @GetMapping("/{groupId}/members/{userId}/status")
    public ResponseEntity<MembershipStatusResponse> getMembershipStatus(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        MembershipStatusResponse resp =
                membershipService.getMembershipStatus(groupId, userId);
        if (resp == null) {
            resp = new MembershipStatusResponse();
        }
        return ResponseEntity.ok(resp);
    }


    @GetMapping("/{groupId}/members/status")
    public ResponseEntity<MembershipStatusResponse> getMembershipStatus(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long currentUserId = userService.extractUserId(authHeader);
        MembershipStatusResponse resp =
                membershipService.getMembershipStatus(groupId, currentUserId);
        if (resp == null) {
            resp = new MembershipStatusResponse();
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    @CrossOrigin(allowedHeaders = "*")
    public Page<GroupResponse> browse(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long domainId,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) List<String> topics,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size
    ) {
        return groupService.browseGroups(name, domainId, isPublic, topics, page, size);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailResponse> getGroupDetails(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long currentUserId = userService.extractUserId(authHeader);
        GroupDetailResponse detail = groupService.getGroupDetails(groupId, currentUserId);
        return ResponseEntity.ok(detail);
    }

    @PutMapping("/{groupId}/settings")
    public ResponseEntity<GroupResponse> updateSettings(
            @PathVariable Long groupId,
            @RequestBody GroupSettingsRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long currentUserId = userService.extractUserId(authHeader);
        Group updated = groupService.updateGroupSettings(groupId, currentUserId, req);

        return ResponseEntity.ok(GroupResponse.fromEntity(updated));
    }
}

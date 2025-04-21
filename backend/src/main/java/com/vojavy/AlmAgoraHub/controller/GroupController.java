package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.dto.requests.CreateGroupRequest;
import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.group.GroupMembership;
import com.vojavy.AlmAgoraHub.service.UserService;
import com.vojavy.AlmAgoraHub.service.group.GroupMembershipService;
import com.vojavy.AlmAgoraHub.service.group.GroupService;
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
    public GroupMembership joinGroup(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        return membershipService.requestToJoinGroup(groupId, userId);
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

    @PutMapping("/{groupId}/members/{targetUserId}/role")
    public GroupMembership changeRole(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long targetUserId,
            @RequestParam String newRole
    ) {
        Long actorId = userService.extractUserId(authHeader);
        membershipService.changeUserRole(groupId, actorId, targetUserId, newRole);
        return membershipService
                .getMembershipInfo(groupId, targetUserId)
                .orElseThrow(() -> new IllegalStateException("Membership not found after role change"));
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
    public List<GroupMembership> listMembers(
            @PathVariable Long groupId,
            @RequestParam(required = false) String status
    ) {
        if (status == null) {
            return membershipService.getMembershipsForGroup(groupId);
        }
        return membershipService.getMembershipsForGroupByStatus(groupId, status);
    }

    @GetMapping("/user")
    public List<Group> listMyGroups(
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        return membershipService.getGroupsForUser(userId);
    }

    @GetMapping("/{groupId}/members/{userId}/status")
    public String getStatus(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        return membershipService.getMembershipStatus(groupId, userId);
    }

    @GetMapping
    public List<Group> browse(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long domainId,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) List<String> topics
    ) {
        return groupService.browseGroups(name, domainId, isPublic, topics);
    }
}

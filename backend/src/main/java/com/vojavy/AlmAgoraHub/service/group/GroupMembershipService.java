package com.vojavy.AlmAgoraHub.service.group;

import com.vojavy.AlmAgoraHub.dto.responses.GroupResponse;
import com.vojavy.AlmAgoraHub.dto.responses.MembershipStatusResponse;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.group.GroupMembership;
import com.vojavy.AlmAgoraHub.repository.group.GroupMembershipRepository;
import com.vojavy.AlmAgoraHub.repository.group.GroupRepository;
import com.vojavy.AlmAgoraHub.service.User.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupMembershipService {

    private final GroupMembershipRepository membershipRepository;
    private final GroupRepository           groupRepository;
    private final UserService               userService;

    protected GroupMembershipService(
            GroupMembershipRepository membershipRepository,
            GroupRepository groupRepository,
            UserService userService
    ) {
        this.membershipRepository = membershipRepository;
        this.groupRepository      = groupRepository;
        this.userService          = userService;
    }

    protected GroupMembership saveMembership(GroupMembership membership) {
        return membershipRepository.save(membership);
    }

    protected GroupMembership joinGroup(Long groupId, Long userId) {
        Group group = findGroupOrThrow(groupId);
        User  user  = findUserOrThrow(userId);

        return membershipRepository.findByUserAndGroup(user, group)
                .orElseGet(() -> {
                    GroupMembership m = new GroupMembership();
                    m.setGroup(group);
                    m.setUser(user);
                    m.setJoinedAt(Instant.now());
                    if (group.isPublic()) {
                        m.setStatus("approved");
                        m.setRole("member");
                    } else {
                        m.setStatus("pending");
                        m.setRole("member");
                    }
                    return membershipRepository.save(m);
                });
    }

    protected void banMember(Long groupId, Long targetUserId) {
        GroupMembership m = findMembershipOrThrow(targetUserId, groupId);
        m.setStatus("banned");
        membershipRepository.save(m);
    }

    protected void unbanMember(Long groupId, Long targetUserId) {
        GroupMembership m = findMembershipOrThrow(targetUserId, groupId);
        m.setStatus("approved");
        m.setRole("member");
        membershipRepository.save(m);
    }

    protected GroupMembership inviteUserToGroup(
            Long groupId,
            Long inviterId,
            Long targetUserId,
            String role
    ) {
        Group group   = findGroupOrThrow(groupId);
        User  target  = findUserOrThrow(targetUserId);

        if (membershipRepository.findByUserAndGroup(target, group).isPresent()) {
            throw new IllegalStateException("User is already in group");
        }

        GroupMembership m = new GroupMembership();
        m.setGroup(group);
        m.setUser(target);
        m.setJoinedAt(Instant.now());
        m.setStatus("approved");
        m.setRole(role);
        return membershipRepository.save(m);
    }

    protected GroupMembership leaveGroup(Long groupId, Long userId) {
        GroupMembership m = findMembershipOrThrow(userId, groupId);
        membershipRepository.delete(m);
        return m;
    }

    protected GroupMembership handleJoinRequest(
            Long groupId,
            Long managerUserId,
            Long targetUserId,
            boolean approve
    ) {
        GroupMembership m = findMembershipOrThrow(targetUserId, groupId);
        if (approve) {
            m.setStatus("approved");
            m.setRole("member");
            return membershipRepository.save(m);
        } else {
            membershipRepository.delete(m);
            return m;
        }
    }

    protected void removeUserFromGroup(
            Long groupId,
            Long managerUserId,
            Long targetUserId
    ) {
        GroupMembership m = findMembershipOrThrow(targetUserId, groupId);
        membershipRepository.delete(m);
    }

    protected GroupMembership changeUserRole(
            Long groupId,
            Long targetUserId,
            String newRole
    ) {
        GroupMembership m = findMembershipOrThrow(targetUserId, groupId);
        m.setRole(newRole);
        return membershipRepository.save(m);
    }

    @Transactional(readOnly = true)
    protected List<GroupMembership> getMembershipsForGroup(Long groupId) {
        Group group = findGroupOrThrow(groupId);
        return membershipRepository.findByGroup(group);
    }

    @Transactional(readOnly = true)
    protected List<GroupMembership> getMembershipsForGroupByStatus(
            Long groupId,
            String status
    ) {
        return getMembershipsForGroup(groupId).stream()
                .filter(m -> status.equals(m.getStatus()))
                .toList();
    }

    @Transactional(readOnly = true)
    protected Page<GroupResponse> getUserGroups(
            Long userId,
            int page,
            int size
    ) {
        User     user    = findUserOrThrow(userId);
        Pageable pageReq = PageRequest.of(page, size);
        return membershipRepository.findByUser(user, pageReq)
                .map(m -> GroupResponse.fromEntity(m.getGroup()));
    }

    @Transactional(readOnly = true)
    protected Optional<GroupMembership> getMembershipInfo(
            Long groupId,
            Long userId
    ) {
        Group group = findGroupOrThrow(groupId);
        User  user  = findUserOrThrow(userId);
        return membershipRepository.findByUserAndGroup(user, group);
    }

    protected MembershipStatusResponse getMembershipStatus(
            Long groupId,
            Long userId
    ) {
        return getMembershipInfo(groupId, userId)
                .map(m -> new MembershipStatusResponse(m.getStatus(), m.getRole()))
                .orElse(new MembershipStatusResponse(null, null));
    }

    protected void approvePendingOnPublic(Long groupId) {
        Group group = findGroupOrThrow(groupId);
        List<GroupMembership> toApprove = membershipRepository.findByGroup(group)
                .stream()
                .filter(m -> "pending".equals(m.getStatus()) && !"invited".equals(m.getRole()))
                .peek(m -> {
                    m.setStatus("approved");
                    m.setRole("member");
                })
                .toList();
        membershipRepository.saveAll(toApprove);
    }

    // --- private helpers ---

    protected boolean hasPermission(String role, String requiredRole) {
        List<String> hierarchy = List.of(
                "pending",
                "member",
//              "editor",
                "helper",
                "admin",
                "owner"
        );

        int currentLevel  = hierarchy.indexOf(role);
        int requiredLevel = hierarchy.indexOf(requiredRole);

        if (currentLevel == -1 || requiredLevel == -1) {
            return false;
        }
        return currentLevel >= requiredLevel;
    }

    protected boolean hasPermission(User user, Group group, String requiredRole) {
        return getMembershipInfo(group.getId(), user.getId())
                .map(membership -> hasPermission(membership.getRole(), requiredRole))
                .orElse(false);
    }

    private Group findGroupOrThrow(Long groupId) {
        return groupRepository.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
    }

    private User findUserOrThrow(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private GroupMembership findMembershipOrThrow(Long userId, Long groupId) {
        return membershipRepository
                .findByUser_IdAndGroup_Id(userId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("Membership not found"));
    }
}
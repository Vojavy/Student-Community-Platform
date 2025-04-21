package com.vojavy.AlmAgoraHub.service.group;

import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.group.GroupMembership;
import com.vojavy.AlmAgoraHub.repository.group.GroupMembershipRepository;
import com.vojavy.AlmAgoraHub.repository.group.GroupRepository;
import com.vojavy.AlmAgoraHub.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupMembershipService {

    private final GroupMembershipRepository membershipRepo;
    private final GroupRepository           groupRepo;
    private final UserService                userService;

    public GroupMembershipService(
            GroupMembershipRepository membershipRepo,
            GroupRepository           groupRepo,
            UserService               userService
    ) {
        this.membershipRepo = membershipRepo;
        this.groupRepo      = groupRepo;
        this.userService    = userService;
    }

    /** 1) Заявка на вступление */
    public GroupMembership requestToJoinGroup(Long groupId, Long userId) {
        Group group = groupRepo.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        User  user  = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return membershipRepo.findByUserAndGroup(user, group)
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
                        m.setRole("pending");
                    }
                    return membershipRepo.save(m);
                });
    }

    /** 2) Приглашение пользователя (сразу approved) */
    public GroupMembership inviteUserToGroup(Long groupId, Long inviterId, Long targetUserId, String role) {
        Group inviterGroup = groupRepo.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        User inviter = userService.findById(inviterId)
                .orElseThrow(() -> new IllegalArgumentException("Inviter not found"));
        if (!hasPermission(inviter, inviterGroup, "admin")) {
            throw new IllegalStateException("No permission to invite members");
        }

        User target = userService.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

        if (membershipRepo.findByUserAndGroup(target, inviterGroup).isPresent()) {
            throw new IllegalStateException("User is already in group");
        }

        GroupMembership m = new GroupMembership();
        m.setGroup(inviterGroup);
        m.setUser(target);
        m.setJoinedAt(Instant.now());
        m.setStatus("approved");
        m.setRole(role);
        return membershipRepo.save(m);
    }

    /** 3) Изменение роли участника */
    public GroupMembership changeUserRole(Long groupId, Long actorId, Long targetUserId, String newRole) {
        Group group = groupRepo.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        User  actor = userService.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("Actor not found"));

        GroupMembership actorM = membershipRepo.findByUserAndGroup(actor, group)
                .orElseThrow(() -> new IllegalArgumentException("Actor not in group"));
        if (!hasPermission(actorM.getRole(), "admin")) {
            throw new IllegalStateException("Insufficient permissions to change roles");
        }

        User target = userService.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));
        GroupMembership targetM = membershipRepo.findByUserAndGroup(target, group)
                .orElseThrow(() -> new IllegalArgumentException("Target user not in group"));

        targetM.setRole(newRole);
        return membershipRepo.save(targetM);
    }

    /** 4) Обработка заявки (approve/reject) */
    public GroupMembership handleJoinRequest(Long groupId,
                                             Long actorId,
                                             Long targetUserId,
                                             boolean approve) {
        Group group = groupRepo.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        User  actor = userService.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("Actor not found"));

        GroupMembership actorM = membershipRepo.findByUserAndGroup(actor, group)
                .orElseThrow(() -> new IllegalArgumentException("Actor not in group"));
        if (!hasPermission(actorM.getRole(), group.getMinRoleForEvents())) {
            throw new IllegalStateException("Insufficient permissions to approve requests");
        }

        User target = userService.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));
        GroupMembership tm = membershipRepo.findByUserAndGroup(target, group)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found"));

        if (!"pending".equals(tm.getStatus())) {
            throw new IllegalStateException("User is not in pending state");
        }

        if (approve) {
            tm.setStatus("approved");
            tm.setRole("member");
            return membershipRepo.save(tm);
        } else {
            membershipRepo.delete(tm);
            return tm;
        }
    }

    /** 5) Удаление участника из группы */
    public void removeUserFromGroup(Long groupId, Long actorId, Long targetUserId) {
        Group group = groupRepo.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        User  actor = userService.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("Actor not found"));

        GroupMembership actorM = membershipRepo.findByUserAndGroup(actor, group)
                .orElseThrow(() -> new IllegalArgumentException("Actor not in group"));
        if (!hasPermission(actorM.getRole(), group.getMinRoleForEvents())) {
            throw new IllegalStateException("Insufficient permissions to remove members");
        }

        User target = userService.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target not found"));
        GroupMembership tm = membershipRepo.findByUserAndGroup(target, group)
                .orElseThrow(() -> new IllegalArgumentException("Target not in group"));

        membershipRepo.delete(tm);
    }

    /** 6) Все участники по ID группы */
    @Transactional(readOnly = true)
    public List<GroupMembership> getMembershipsForGroup(Long groupId) {
        Group group = groupRepo.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        return membershipRepo.findByGroup(group);
    }

    /** 6.1) По статусу */
    @Transactional(readOnly = true)
    public List<GroupMembership> getMembershipsForGroupByStatus(Long groupId, String status) {
        return getMembershipsForGroup(groupId).stream()
                .filter(m -> status.equals(m.getStatus()))
                .collect(Collectors.toList());
    }

    /** 7) Все группы по ID пользователя */
    @Transactional(readOnly = true)
    public List<Group> getGroupsForUser(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return membershipRepo.findByUser(user).stream()
                .map(GroupMembership::getGroup)
                .collect(Collectors.toList());
    }

    /** 8) Информация о статусе/роли конкретного пользователя в группе */
    @Transactional(readOnly = true)
    public Optional<GroupMembership> getMembershipInfo(Long groupId, Long userId) {
        Group group = groupRepo.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        User  user  = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return membershipRepo.findByUserAndGroup(user, group);
    }

    /** 8.1) Только статус */
    @Transactional(readOnly = true)
    public String getMembershipStatus(Long groupId, Long userId) {
        return getMembershipInfo(groupId, userId)
                .map(GroupMembership::getStatus)
                .orElse(null);
    }

    // === вспомогательные методы ===

    /** Сохранить произвольную запись */
    public GroupMembership saveMembership(GroupMembership membership) {
        return membershipRepo.save(membership);
    }

    /** Проверка прав по иерархии ролей */
    public boolean hasPermission(String role, String required) {
        List<String> hierarchy = List.of("pending", "member", "editor", "helper", "admin", "owner");
        return hierarchy.indexOf(role) >= hierarchy.indexOf(required);
    }

    public boolean hasPermission(User user, Group group, String requiredRole) {
        return getMembershipInfo(group.getId(), user.getId())
                .map(m -> hasPermission(m.getRole(), requiredRole))
                .orElse(false);
    }
}

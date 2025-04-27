package com.vojavy.AlmAgoraHub.service.group;

import com.vojavy.AlmAgoraHub.dto.requests.CreateGroupRequest;
import com.vojavy.AlmAgoraHub.dto.requests.GroupPostRequest;
import com.vojavy.AlmAgoraHub.dto.requests.GroupSettingsRequest;
import com.vojavy.AlmAgoraHub.dto.responses.*;
import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.model.group.*;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.repository.group.GroupRepository;
import com.vojavy.AlmAgoraHub.service.UniversityDomainService;
import com.vojavy.AlmAgoraHub.service.user.UserISDataService;
import com.vojavy.AlmAgoraHub.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    private final GroupRepository         groupRepository;
    private final GroupMembershipService  membershipService;
    private final UniversityDomainService domainService;
    private final UserService             userService;
    private final UserISDataService       userISDataService;
    private final GroupPostService        groupPostService;

    public GroupService(
            GroupRepository groupRepository,
            GroupMembershipService membershipService,
            UniversityDomainService domainService,
            UserService userService,
            UserISDataService userISDataService,
            GroupPostService groupPostService
    ) {
        this.groupRepository   = groupRepository;
        this.membershipService = membershipService;
        this.domainService     = domainService;
        this.userService       = userService;
        this.userISDataService = userISDataService;
        this.groupPostService  = groupPostService;
    }

    // ----------------------------
    // 1) Group driving
    // ----------------------------

    public Group createGroup(CreateGroupRequest req, Long creatorId) {
        User creator   = findUserOrThrow(creatorId);
        UniversityDomain domain = findDomainOrNull(req.getDomain());

        Group g = new Group();
        g.setName(req.getName());
        g.setDescription(req.getDescription());
        g.setTopics(req.getTopics());
        g.setCreatedAt(Instant.now());
        g.setPublic(req.isPublic());
        g.setMinRoleForPosts(req.getMinRoleForPosts());
        g.setMinRoleForEvents(req.getMinRoleForEvents());
        g.setDomain(domain);

        Group saved = groupRepository.save(g);
        membershipService.saveMembership(
                new GroupMembership(creator, saved, "owner", "approved", Instant.now())
        );
        return saved;
    }

    public void deleteGroup(Long actorId, Long groupId) {
        Group group = findGroupOrThrow(groupId);
        validatePermission(actorId, group, "owner");
        groupRepository.deleteById(Math.toIntExact(groupId));
    }

    // -----------------------------------
    // 2) Join / Leave / Invite
    // -----------------------------------

    public GroupMembershipResponse requestToJoinGroup(Long groupId, Long userId) {
        GroupMembership m = membershipService.joinGroup(groupId, userId);
        return toDto(m);
    }

    public GroupMembershipResponse requestToLeaveGroup(Long groupId, Long userId) {
        membershipService.getMembershipInfo(groupId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Not a member"));
        GroupMembership m = membershipService.leaveGroup(groupId, userId);
        return toDto(m);
    }

    public GroupMembershipResponse inviteUserToGroup(
            Long inviterId, Long groupId, Long targetUserId, String role
    ) {
        Group group = findGroupOrThrow(groupId);
        validatePermission(inviterId, group, "admin");
        GroupMembership m = membershipService.inviteUserToGroup(groupId, inviterId, targetUserId, role);
        return toDto(m);
    }

    // ----------------------------------
    // 3) Members driving (admin)
    // ----------------------------------

    public GroupMembershipResponse changeUserRole(
            Long actorId, Long groupId, Long targetUserId, String newRole
    ) {
        Group group = findGroupOrThrow(groupId);
        validatePermission(actorId, group, group.getMinRoleForEvents());
        GroupMembership m = membershipService.changeUserRole(groupId, targetUserId, newRole);
        return toDto(m);
    }

    public GroupMembershipResponse handleJoinRequest(
            Long managerId, Long groupId, Long targetUserId, boolean approve
    ) {
        Group group = findGroupOrThrow(groupId);
        validatePermission(managerId, group, group.getMinRoleForEvents());
        GroupMembership m = membershipService.handleJoinRequest(groupId, managerId, targetUserId, approve);
        return toDto(m);
    }

    public void removeMember(Long managerId, Long groupId, Long targetUserId) {
        Group group = findGroupOrThrow(groupId);
        validatePermission(managerId, group, group.getMinRoleForEvents());
        membershipService.removeUserFromGroup(groupId, managerId, targetUserId);
    }

    public void banMember(Long actorId, Long groupId, Long targetUserId) {
        Group group = findGroupOrThrow(groupId);
        validatePermission(actorId, group, group.getMinRoleForEvents());
        membershipService.banMember(groupId, targetUserId);
    }

    public void unbanMember(Long actorId, Long groupId, Long targetUserId) {
        Group group = findGroupOrThrow(groupId);
        validatePermission(actorId, group, group.getMinRoleForEvents());
        membershipService.unbanMember(groupId, targetUserId);
    }

    // ------------------------------------------------
    // 4) Get: memberships, user groups, user group status
    // ------------------------------------------------

    public List<GroupMembershipResponse> getGroupMembers(
            Long groupId, Long currentUserId, String status
    ) {
        Group group = findGroupOrThrow(groupId);
        if (!group.isPublic()) {
            membershipService.getMembershipInfo(groupId, currentUserId)
                    .filter(m -> "approved".equals(m.getStatus()))
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN, "Private group"));
        }
        List<GroupMembership> raw = (status == null)
                ? membershipService.getMembershipsForGroup(groupId)
                : membershipService.getMembershipsForGroupByStatus(groupId, status);
        return raw.stream().map(this::toDto).toList();
    }

    public Page<GroupResponse> getUserGroups(Long userId, int page, int size) {
        return membershipService.getUserGroups(userId, page, size);
    }

    public MembershipStatusResponse getMembershipStatus(
            Long actorId, Long targetUserId, Long groupId
    ) {
        // actorId needed for Verification
        return membershipService.getMembershipStatus(groupId, targetUserId);
    }

    // ----------------------------
    // 5) Update group
    // ----------------------------

    public Group updateGroupSettings(
            Long groupId, Long adminId, GroupSettingsRequest req
    ) {
        Group group = findGroupOrThrow(groupId);
        validatePermission(adminId, group, "admin");

        boolean wasPublic = group.isPublic();
        group.setName(req.getName());
        group.setDescription(req.getDescription());
        group.setPublic(req.isPublic());
        group.setMinRoleForPosts(req.getMinRoleForPosts());
        group.setMinRoleForEvents(req.getMinRoleForEvents());
        group.setTopics(req.getTopics());
        group.setDomain(findDomainOrNull(req.getDomain()));

        Group saved = groupRepository.save(group);
        if (!wasPublic && saved.isPublic()) {
            membershipService.approvePendingOnPublic(groupId);
        }
        return saved;
    }

    // ----------------------------
    // 6) Browsing & details
    // ----------------------------

    public Page<GroupResponse> browseGroups(
            String name, Long domainId, Boolean isPublic, List<String> topics, int page, int size
    ) {
        String[] tarr = (topics == null || topics.isEmpty())
                ? null
                : topics.toArray(String[]::new);
        Pageable p = PageRequest.of(page, size);
        List<Group> all = groupRepository.findByFilters(name, domainId, isPublic, tarr);
        int start = (int)Math.min(p.getOffset(), all.size());
        int end   = Math.min(start + p.getPageSize(), all.size());
        List<GroupResponse> content = all.subList(start, end)
                .stream()
                .map(GroupResponse::fromEntity)
                .toList();
        return new PageImpl<>(content, p, all.size());
    }

    public ResponseEntity<GroupDetailResponse> getGroupDetails(
            Long groupId,
            Long currentUserId
    ) {
        Group group = findGroupOrThrow(groupId);
        Optional<GroupMembership> meOpt =
                membershipService.getMembershipInfo(groupId, currentUserId);

        boolean isPrivateNonMember = !group.isPublic() &&
                (meOpt.isEmpty() || !"approved".equals(meOpt.get().getStatus()));
        boolean isBanned = meOpt.filter(m -> "banned".equals(m.getStatus())).isPresent();

        List<GroupMembership> approvedAll = membershipService.getMembershipsForGroup(groupId)
                .stream()
                .filter(m -> "approved".equals(m.getStatus()))
                .toList();

        UserSummaryResponse owner  = findByRole(approvedAll, "owner");
        List<UserSummaryResponse> admins = findAllByRole(approvedAll, "admin");

        if (isPrivateNonMember || isBanned) {
            GroupDetailResponse limited = new GroupDetailResponse(
                    group.getId(),
                    group.getName(),
                    /*Description=*/null,
                    Collections.singletonList(group.getTopics()),
                    group.isPublic(),
                    /*minRoleForPosts=*/null,
                    /*minRoleForEvents=*/null,
                    group.getDomain(),
                    group.getCreatedAt(),
                    owner,
                    admins,
                    /*helpers=*/Collections.emptyList(),
                    /*members=*/Collections.emptyList()
            );
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(limited);
        }

        List<UserSummaryResponse> helpers = findAllByRole(approvedAll, "helper");
        List<UserSummaryResponse> members = approvedAll.stream()
                .filter(m -> !List.of("owner", "admin", "helper", "invited").contains(m.getRole()))
                .sorted(Comparator.comparing(GroupMembership::getJoinedAt).reversed())
                .map(m -> toUserSummary(m.getUser()))
                .toList();

        GroupDetailResponse full = new GroupDetailResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                Collections.singletonList(group.getTopics()),
                group.isPublic(),
                group.getMinRoleForPosts(),
                group.getMinRoleForEvents(),
                group.getDomain(),
                group.getCreatedAt(),
                owner,
                admins,
                helpers,
                members
        );
        return ResponseEntity.ok(full);
    }

    // ----------------------------
    // 6) Posts
    // ----------------------------

    public Page<GroupPost> getPostsForGroup(
            Long groupId,
            Long userId,
            int page,
            int size,
            String search
    ) {
        Group g = findGroupOrThrow(groupId);
        validateGroupVisibleOrMember(userId, g);
        validatePermission(userId, g, g.getMinRoleForPosts());
        return groupPostService.getPostsForGroup(g, page, size, search);
    }

    public GroupPost getPost(
            Long groupId, Long postId, Long userId
    ) {
        Group g = findGroupOrThrow(groupId);
        validateGroupVisibleOrMember(userId, g);
        return groupPostService.getPost(g, postId);
    }

    public GroupPost createPost(
            Long groupId, Long userId, GroupPostRequest req
    ) {
        Group g = findGroupOrThrow(groupId);
        validateGroupVisibleOrMember(userId, g);
        validatePermission(userId, g, g.getMinRoleForPosts());
        User u = findUserOrThrow(userId);
        return groupPostService.createPost(g, u, req);
    }

    public GroupPost updatePost(
            Long groupId, Long postId, Long userId, GroupPostRequest req
    ) {
        Group g = findGroupOrThrow(groupId);
        validateGroupVisibleOrMember(userId, g);
        GroupPost existing = groupPostService.getPost(g, postId);
        // only author or event-level:
        if (! existing.getUser().getId().equals(userId)) {
            validatePermission(userId, g, g.getMinRoleForEvents());
        }
        return groupPostService.updatePost(existing, req);
    }

    public void deletePost(
            Long groupId, Long postId, Long userId
    ) {
        Group g = findGroupOrThrow(groupId);
        validateGroupVisibleOrMember(userId, g);
        GroupPost existing = groupPostService.getPost(g, postId);
        if (! existing.getUser().getId().equals(userId)) {
            validatePermission(userId, g, g.getMinRoleForEvents());
        }
        groupPostService.deletePost(postId);
    }


    public UserSummaryResponse toUserSummary(User u) {
        return userISDataService.getByUserId(u.getId())
                .map(d -> new UserSummaryResponse(
                        u.getId(),
                        u.getUsername(),
                        d.getJmeno() + " " + d.getPrijmeni()
                ))
                .orElseGet(() -> new UserSummaryResponse(
                        u.getId(),
                        null,
                        u.getUsername()
                ));
    }

    // ================
    // private helpers
    // ================

    private void validateGroupVisibleOrMember(Long userId, Group g) {
        Optional<GroupMembership> me = membershipService.getMembershipInfo(g.getId(), userId);
        if (g.isPublic()) {
            if (me.filter(m -> "banned".equals(m.getStatus())).isPresent()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are banned");
            }
        } else {
            if (me.isEmpty() || !"approved".equals(me.get().getStatus())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Private group");
            }
        }
    }

    private Group findGroupOrThrow(Long id) {
        return groupRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Group not found"));
    }

    private User findUserOrThrow(Long id) {
        return userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
    }

    private UniversityDomain findDomainOrNull(String code) {
        if (code == null) return null;
        return domainService.getDomainByCode(code)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Domain not found"));
    }

    private void validatePermission(Long userId, Group group, String requiredRole) {
        User u = findUserOrThrow(userId);
        if (!membershipService.hasPermission(u, group, requiredRole)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Insufficient permissions");
        }
    }

    private GroupMembershipResponse toDto(GroupMembership m) {
        return new GroupMembershipResponse(
                m.getId(),
                toUserSummary(m.getUser()),
                m.getRole(),
                m.getStatus(),
                m.getJoinedAt()
        );
    }

    private List<UserSummaryResponse> findAllByRole(
            List<GroupMembership> list, String role
    ) {
        return list.stream()
                .filter(m -> role.equals(m.getRole()))
                .map(gm -> toUserSummary(gm.getUser()))
                .toList();
    }

    private UserSummaryResponse findByRole(
            List<GroupMembership> list, String role
    ) {
        return list.stream()
                .filter(m -> role.equals(m.getRole()))
                .findFirst()
                .map(gm -> toUserSummary(gm.getUser()))
                .orElse(null);
    }
}
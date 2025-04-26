package com.vojavy.AlmAgoraHub.service.group;

import com.vojavy.AlmAgoraHub.dto.requests.CreateGroupRequest;
import com.vojavy.AlmAgoraHub.dto.requests.GroupSettingsRequest;
import com.vojavy.AlmAgoraHub.dto.responses.GroupDetailResponse;
import com.vojavy.AlmAgoraHub.dto.responses.GroupMembershipResponse;
import com.vojavy.AlmAgoraHub.dto.responses.GroupResponse;
import com.vojavy.AlmAgoraHub.dto.responses.UserSummaryResponse;
import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.group.GroupMembership;
import com.vojavy.AlmAgoraHub.repository.group.GroupRepository;
import com.vojavy.AlmAgoraHub.service.UniversityDomainService;
import com.vojavy.AlmAgoraHub.service.UserISDataService;
import com.vojavy.AlmAgoraHub.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository           groupRepository;
    private final GroupMembershipService    membershipService;
    private final UniversityDomainService   domainService;
    private final UserService               userService;
    private final UserISDataService         userISDataService;

    public GroupService(
            GroupRepository groupRepository,
            GroupMembershipService membershipService,
            UniversityDomainService domainService,
            UserService userService,
            UserISDataService userISDataService
    ) {
        this.groupRepository  = groupRepository;
        this.membershipService = membershipService;
        this.domainService    = domainService;
        this.userService      = userService;
        this.userISDataService = userISDataService;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Optional<Group> getGroupById(Long groupId) {
        return groupRepository.findById(Math.toIntExact(groupId));
    }

    public Group createGroup(CreateGroupRequest request, Long creatorUserId) {
        User creator = findUserOrThrow(creatorUserId);
        UniversityDomain domain = findDomainOrNull(request.getDomain());

        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setTopics(request.getTopics());
        group.setCreatedAt(Instant.now());
        group.setPublic(request.isPublic());
        group.setMinRoleForPosts(request.getMinRoleForPosts());
        group.setMinRoleForEvents(request.getMinRoleForEvents());
        group.setDomain(domain);

        Group saved = groupRepository.save(group);
        // сразу добавляем владельца
        membershipService.saveMembership(
                new GroupMembership(creator, saved, "owner", "approved", Instant.now())
        );
        return saved;
    }

    public GroupMembershipResponse requestToJoinGroup(Long groupId, Long userId) {
        GroupMembership groupMembership = membershipService.joinGroup(groupId, userId);
        return new GroupMembershipResponse(
                groupMembership.getId(),
                toUserSummary(groupMembership.getUser()),
                groupMembership.getRole(),
                groupMembership.getStatus(),
                groupMembership.getJoinedAt()
        );
    }

    public void handleMembershipRequest(
            Long managerUserId,
            Long groupId,
            Long targetUserId,
            boolean approve
    ) {
        Group group = findGroupOrThrow(groupId);
        validateGroupPermission(managerUserId, group, group.getMinRoleForEvents());
        membershipService.handleJoinRequest(groupId, managerUserId, targetUserId, approve);
    }

    public GroupMembershipResponse requestToLeaveGroup(Long groupId, Long userId) {
        membershipService.getMembershipInfo(groupId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "User is not a member of this group"));

        GroupMembership deleted = membershipService.leaveGroup(groupId, userId);

        return new GroupMembershipResponse(
                deleted.getId(),
                toUserSummary(deleted.getUser()),
                deleted.getRole(),
                deleted.getStatus(),
                deleted.getJoinedAt()
        );
    }

    public void removeMember(
            Long managerUserId,
            Long groupId,
            Long targetUserId
    ) {
        Group group = findGroupOrThrow(groupId);
        validateGroupPermission(managerUserId, group, group.getMinRoleForEvents());
        membershipService.removeUserFromGroup(groupId, managerUserId, targetUserId);
    }

    public GroupMembershipResponse changeUserRole(
            Long actingUserId,
            Long groupId,
            Long targetUserId,
            String newRole
    ) {
        Group group = findGroupOrThrow(groupId);

        validateGroupPermission(actingUserId, group, group.getMinRoleForEvents());

        GroupMembership updated = membershipService.changeUserRole(
                groupId,
                targetUserId,
                newRole
        );

        return new GroupMembershipResponse(
                updated.getId(),
                toUserSummary(updated.getUser()),
                updated.getRole(),
                updated.getStatus(),
                updated.getJoinedAt()
        );
    }

    public Page<GroupResponse> browseGroups(
            String name,
            Long domainId,
            Boolean isPublic,
            List<String> topics,
            int page,
            int size
    ) {
        String[] topicsArray = (topics == null || topics.isEmpty())
                ? null
                : topics.toArray(String[]::new);

        Pageable pageable = PageRequest.of(page, size);
        List<Group> raw = groupRepository.findByFilters(
                name, domainId, isPublic, topicsArray
        );

        int start = (int) Math.min(pageable.getOffset(), raw.size());
        int end   = Math.min(start + pageable.getPageSize(), raw.size());

        List<GroupResponse> content = raw.subList(start, end)
                .stream()
                .map(GroupResponse::fromEntity)
                .toList();

        return new PageImpl<>(content, pageable, raw.size());
    }

    public GroupDetailResponse getGroupDetails(Long groupId, Long currentUserId) {
        Group group = findGroupOrThrow(groupId);
        Optional<GroupMembership> membershipOpt =
                membershipService.getMembershipInfo(groupId, currentUserId);

        if (group.isPublic()) {
            if (membershipOpt.filter(m -> "banned".equals(m.getStatus())).isPresent()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Banned users can't see public groups");
            }
        } else {
            if (membershipOpt.filter(m -> !"approved".equals(m.getStatus())).isPresent()
                    || membershipOpt.isEmpty()
            ) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Only approved members can see private groups");
            }
        }

        List<GroupMembership> allApproved = membershipService
                .getMembershipsForGroup(groupId).stream()
                .filter(m -> "approved".equals(m.getStatus()))
                .toList();

        UserSummaryResponse owner = findByRole(allApproved, "owner");
        List<UserSummaryResponse> admins  = findAllByRole(allApproved, "admin");
        List<UserSummaryResponse> helpers = findAllByRole(allApproved, "helper");
        List<UserSummaryResponse> members = allApproved.stream()
                .filter(m -> List.of("owner","admin","helper").contains(m.getRole()) == false)
                .sorted(Comparator.comparing(GroupMembership::getJoinedAt).reversed())
                .map(m -> toUserSummary(m.getUser()))
                .toList();

        return new GroupDetailResponse(
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
    }

    public List<GroupMembershipResponse> getGroupMembers(
            Long groupId,
            Long currentUserId,
            String status
    ) {
        Group group = findGroupOrThrow(groupId);

        if (!group.isPublic()) {
            membershipService.getMembershipInfo(groupId, currentUserId)
                    .filter(m -> "approved".equals(m.getStatus()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        }

        List<GroupMembership> raw = status == null
                ? membershipService.getMembershipsForGroup(groupId)
                : membershipService.getMembershipsForGroupByStatus(groupId, status);

        return raw.stream()
                .map(m -> new GroupMembershipResponse(
                        m.getId(),
                        toUserSummary(m.getUser()),
                        m.getRole(),
                        m.getStatus(),
                        m.getJoinedAt()
                ))
                .toList();
    }

    public Group updateGroupSettings(
            Long groupId,
            Long adminUserId,
            GroupSettingsRequest request
    ) {
        Group group = findGroupOrThrow(groupId);
        validateGroupPermission(adminUserId, group, "admin");

        boolean wasPublic = group.isPublic();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setPublic(request.isPublic());
        group.setMinRoleForPosts(request.getMinRoleForPosts());
        group.setMinRoleForEvents(request.getMinRoleForEvents());
        group.setTopics(request.getTopics());
        group.setDomain(findDomainOrNull(request.getDomain()));

        Group saved = groupRepository.save(group);
        if (!wasPublic && saved.isPublic()) {
            membershipService.approvePendingOnPublic(groupId);
        }
        return saved;
    }

    // === Private helpful methods ===

    private Group findGroupOrThrow(Long groupId) {
        return groupRepository.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
    }

    private User findUserOrThrow(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UniversityDomain findDomainOrNull(String domainCode) {
        if (domainCode == null) return null;
        return domainService.getDomainByCode(domainCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "University domain not found"));
    }

    private void validateGroupPermission(
            Long userId, Group group, String requiredRole
    ) {
        User user = findUserOrThrow(userId);
        if (!membershipService.hasPermission(user, group, requiredRole)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Insufficient group permissions");
        }
    }

    private UserSummaryResponse findByRole(
            List<GroupMembership> memberships, String role
    ) {
        return memberships.stream()
                .filter(m -> role.equals(m.getRole()))
                .findFirst()
                .map(m -> toUserSummary(m.getUser()))
                .orElse(null);
    }

    private List<UserSummaryResponse> findAllByRole(
            List<GroupMembership> memberships, String role
    ) {
        return memberships.stream()
                .filter(m -> role.equals(m.getRole()))
                .map(m -> toUserSummary(m.getUser()))
                .toList();
    }

    private UserSummaryResponse toUserSummary(User user) {
        return userISDataService.getByUserId(user.getId())
                .map(data -> new UserSummaryResponse(
                        user.getId(),
                        user.getUsername(),
                        data.getJmeno() + " " + data.getPrijmeni()
                ))
                .orElseGet(() -> new UserSummaryResponse(
                        user.getId(),
                        null,
                        user.getUsername()
                ));
    }
}

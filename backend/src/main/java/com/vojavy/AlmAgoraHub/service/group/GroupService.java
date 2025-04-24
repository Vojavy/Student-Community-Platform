package com.vojavy.AlmAgoraHub.service.group;

import com.vojavy.AlmAgoraHub.dto.requests.CreateGroupRequest;
import com.vojavy.AlmAgoraHub.dto.responses.GroupDetailResponse;
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
    private final GroupRepository groupRepository;
    private final GroupMembershipService membershipService;
    private final UserService userService;
    private final UniversityDomainService domainService;
    private final UserISDataService userISDataService;

    public GroupService(
            GroupRepository groupRepository,
            GroupMembershipService membershipService,
            UserService userService,
            UniversityDomainService domainService,
            UserISDataService userISDataService
    ) {
        this.groupRepository = groupRepository;
        this.membershipService = membershipService;
        this.userService = userService;
        this.domainService = domainService;
        this.userISDataService = userISDataService;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Optional<Group> getGroupById(Long id) {
        return groupRepository.findById(Math.toIntExact(id));
    }

    public Group createGroup(CreateGroupRequest request, Long creatorUserId) {
        User creator = userService.findById(creatorUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setTopics(request.getTopics());
        group.setCreatedAt(Instant.now());
        group.setPublic(request.isPublic());
        group.setMinRoleForPosts(request.getMinRoleForPosts());
        group.setMinRoleForEvents(request.getMinRoleForEvents());

        if (request.getDomain() != null) {
            UniversityDomain domain = domainService.getDomainByCode(request.getDomain())
                    .orElseThrow(() -> new IllegalArgumentException("University domain not found"));
            group.setDomain(domain);
        }

        Group savedGroup = groupRepository.save(group);

        membershipService.saveMembership(new GroupMembership(
                creator, savedGroup, "owner", "approved", Instant.now()
        ));

        return savedGroup;
    }

    public void joinGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (membershipService.getMembershipInfo(groupId, userId).isPresent()) {
            throw new IllegalStateException("User already in group");
        }

        membershipService.requestToJoinGroup(groupId, userId);
    }

    public void handleMembershipRequest(Long actingUserId, Long groupId, Long targetUserId, boolean approve) {
        Group group = groupRepository.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        User actor = userService.findById(actingUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!membershipService.hasPermission(actor, group, group.getMinRoleForEvents())) {
            throw new IllegalStateException("Insufficient permissions to manage members");
        }

        membershipService.handleJoinRequest(groupId, actingUserId, targetUserId, approve);
    }

    public void removeMember(Long actingUserId, Long groupId, Long targetUserId) {
        Group group = groupRepository.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        User actor = userService.findById(actingUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!membershipService.hasPermission(actor, group, group.getMinRoleForEvents())) {
            throw new IllegalStateException("Insufficient permissions to remove members");
        }

        membershipService.removeUserFromGroup(groupId, actingUserId, targetUserId);
    }

    public Group updateGroup(Group group) {
        return groupRepository.save(group);
    }

    public void deleteGroup(int id) {
        groupRepository.deleteById(id);
    }

    /** Найти по домену и топикам */
    public List<GroupResponse> findGroupsByDomainAndTopics(Long domainId, String[] topics) {
        return groupRepository.findByDomainIdAndTopics(domainId, topics)
                .stream()
                .map(GroupResponse::fromEntity)
                .collect(Collectors.toList());
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

        List<Group> filtered = groupRepository.findByFilters(
                name, domainId, isPublic, topicsArray
        );

        int fromIndex = (int) Math.min(pageable.getOffset(), filtered.size());
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), filtered.size());
        List<GroupResponse> pageContent = filtered.subList(fromIndex, toIndex)
                .stream()
                .map(GroupResponse::fromEntity)
                .toList();

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    public GroupDetailResponse getGroupDetails(Long groupId, Long currentUserId) {
        Group group = groupRepository.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // проверяем статус membership у currentUser
        boolean isMemberApproved = membershipService
                .getMembershipInfo(groupId, currentUserId)
                .filter(m -> "approved".equals(m.getStatus()))
                .isPresent();

        if (!isMemberApproved) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        List<GroupMembership> approvedAll = membershipService
                .getMembershipsForGroup(groupId).stream()
                .filter(m -> "approved".equals(m.getStatus()))
                .toList();

        UserSummaryResponse owner = approvedAll.stream()
                .filter(m -> "owner".equals(m.getRole()))
                .map(m -> toSummary(m.getUser()))
                .findFirst()
                .orElse(null);

        List<UserSummaryResponse> admins = approvedAll.stream()
                .filter(m -> "admin".equals(m.getRole()))
                .map(m -> toSummary(m.getUser()))
                .toList();

        List<UserSummaryResponse> helpers = approvedAll.stream()
                .filter(m -> "helper".equals(m.getRole()))
                .map(m -> toSummary(m.getUser()))
                .toList();

        List<UserSummaryResponse> members = approvedAll.stream()
                .filter(m -> !List.of("owner","admin","helper").contains(m.getRole()))
                .sorted(Comparator.comparing(GroupMembership::getJoinedAt).reversed())
                .limit(10)
                .map(m -> toSummary(m.getUser()))
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

    private UserSummaryResponse toSummary(User u) {
        return userISDataService.getByUserId(u.getId())
                .map(data -> new UserSummaryResponse(
                        u.getId(),
                        data.getJmeno(),
                        data.getPrijmeni()
                ))
                .orElseGet(() -> new UserSummaryResponse(
                        u.getId(),
                        u.getUsername(),
                        ""
                ));
    }
}

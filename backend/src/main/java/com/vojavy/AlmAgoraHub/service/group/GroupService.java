package com.vojavy.AlmAgoraHub.service.group;

import com.vojavy.AlmAgoraHub.dto.requests.CreateGroupRequest;
import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.group.GroupMembership;
import com.vojavy.AlmAgoraHub.repository.group.GroupRepository;
import com.vojavy.AlmAgoraHub.service.UniversityDomainService;
import com.vojavy.AlmAgoraHub.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMembershipService membershipService;
    private final UserService userService;
    private final UniversityDomainService domainService;

    public GroupService(GroupRepository groupRepository,
                        GroupMembershipService membershipService,
                        UserService userService,
                        UniversityDomainService domainService) {
        this.groupRepository = groupRepository;
        this.membershipService = membershipService;
        this.userService = userService;
        this.domainService = domainService;
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

    public List<Group> findGroupsByDomainAndTopics(Long domainId, String[] topics) {
        return groupRepository.findByDomainIdAndTopics(domainId, topics);
    }

    public List<Group> browseGroups(
            String name,
            Long domainId,
            Boolean isPublic,
            List<String> topics
    ) {
        String[] topicsArray = (topics == null || topics.isEmpty())
                ? null
                : topics.toArray(String[]::new);

        return groupRepository.findByFilters(
                name,
                domainId,
                isPublic,
                topicsArray
        );
    }
}

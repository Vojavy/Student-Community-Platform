package com.vojavy.AlmAgoraHub.repository.group;

import com.vojavy.AlmAgoraHub.model.group.GroupMembership;
import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Integer> {
    Optional<GroupMembership> findByUserAndGroup(User user, Group group);
    List<GroupMembership> findByUser(User user);
    List<GroupMembership> findByGroup(Group group);
}

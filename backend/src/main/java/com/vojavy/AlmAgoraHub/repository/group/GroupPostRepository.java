package com.vojavy.AlmAgoraHub.repository.group;

import com.vojavy.AlmAgoraHub.model.group.GroupPost;
import com.vojavy.AlmAgoraHub.model.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupPostRepository extends JpaRepository<GroupPost, Integer> {
    List<GroupPost> findByGroup(Group group);
}

package com.vojavy.AlmAgoraHub.repository.group;

import com.vojavy.AlmAgoraHub.model.group.GroupAttachment;
import com.vojavy.AlmAgoraHub.model.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupAttachmentRepository extends JpaRepository<GroupAttachment, Integer> {
    List<GroupAttachment> findByGroup(Group group);
}

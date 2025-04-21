package com.vojavy.AlmAgoraHub.repository.group;

import com.vojavy.AlmAgoraHub.model.group.GroupAnnouncement;
import com.vojavy.AlmAgoraHub.model.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupAnnouncementRepository extends JpaRepository<GroupAnnouncement, Integer> {
    List<GroupAnnouncement> findByGroup(Group group);
}

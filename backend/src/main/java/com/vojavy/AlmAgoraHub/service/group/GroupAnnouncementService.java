package com.vojavy.AlmAgoraHub.service.group;

import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.group.GroupAnnouncement;
import com.vojavy.AlmAgoraHub.repository.group.GroupAnnouncementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupAnnouncementService {

    private final GroupAnnouncementRepository announcementRepository;

    public GroupAnnouncementService(GroupAnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public List<GroupAnnouncement> getAnnouncementsForGroup(Group group) {
        return announcementRepository.findByGroup(group);
    }

    public Optional<GroupAnnouncement> getAnnouncementById(int id) {
        return announcementRepository.findById(id);
    }

    public GroupAnnouncement createAnnouncement(GroupAnnouncement announcement) {
        return announcementRepository.save(announcement);
    }

    public GroupAnnouncement updateAnnouncement(GroupAnnouncement announcement) {
        return announcementRepository.save(announcement);
    }

    public void deleteAnnouncement(int id) {
        announcementRepository.deleteById(id);
    }
}

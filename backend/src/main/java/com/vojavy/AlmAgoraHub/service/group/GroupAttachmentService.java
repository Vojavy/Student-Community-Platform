package com.vojavy.AlmAgoraHub.service.group;

import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.group.GroupAttachment;
import com.vojavy.AlmAgoraHub.repository.group.GroupAttachmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupAttachmentService {

    private final GroupAttachmentRepository attachmentRepository;

    public GroupAttachmentService(GroupAttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public List<GroupAttachment> getAttachmentsForGroup(Group group) {
        return attachmentRepository.findByGroup(group);
    }

    public Optional<GroupAttachment> getAttachmentById(int id) {
        return attachmentRepository.findById(id);
    }

    public GroupAttachment createAttachment(GroupAttachment attachment) {
        return attachmentRepository.save(attachment);
    }

    public GroupAttachment updateAttachment(GroupAttachment attachment) {
        return attachmentRepository.save(attachment);
    }

    public void deleteAttachment(int id) {
        attachmentRepository.deleteById(id);
    }
}

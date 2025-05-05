package com.vojavy.AlmAgoraHub.repository;

import com.vojavy.AlmAgoraHub.model.user.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {
    List<UserMessage> findBySender_IdAndRecipient_IdOrderByCreatedAtAsc(Long senderId, Long recipientId);
    List<UserMessage> findByParentMessage_IdOrderByCreatedAtAsc(Long parentMessageId);
}

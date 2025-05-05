// src/main/java/com/vojavy/AlmAgoraHub/service/user/UserMessageService.java
package com.vojavy.AlmAgoraHub.service.user;

import com.vojavy.AlmAgoraHub.dto.requests.SendMessageRequest;
import com.vojavy.AlmAgoraHub.dto.responses.MessageResponse;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.user.UserMessage;
import com.vojavy.AlmAgoraHub.repository.UserMessageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserMessageService {

    private final UserMessageRepository repo;
    private final UserService userService;

    public UserMessageService(
            UserMessageRepository repo,
            UserService userService
    ) {
        this.repo        = repo;
        this.userService = userService;
    }

    public MessageResponse sendMessage(Long senderId, SendMessageRequest req) {
        User sender    = userService.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User recipient = userService.findById(req.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        UserMessage msg = new UserMessage();
        msg.setSender(sender);
        msg.setRecipient(recipient);
        msg.setContentText(req.getContentText());
        msg.setContentBase64(req.getContentBase64());
        msg.setCreatedAt(Instant.now());

        if (req.getParentMessageId() != null) {
            UserMessage parent = repo.findById(req.getParentMessageId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent message not found"));
            msg.setParentMessage(parent);
        }

        UserMessage saved = repo.save(msg);
        return toDto(saved);
    }

    public MessageResponse markAsRead(Long messageId, Long userId) {
        UserMessage msg = repo.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if (! msg.getRecipient().getId().equals(userId)) {
            throw new IllegalArgumentException("Only recipient can mark as read");
        }

        if (!msg.isRead()) {
            msg.setRead(true);
            msg.setReadAt(Instant.now());
            msg = repo.save(msg);
        }

        return toDto(msg);
    }

    public List<MessageResponse> getConversation(Long userId1, Long userId2) {
        List<UserMessage> sent     = repo.findBySender_IdAndRecipient_IdOrderByCreatedAtAsc(userId1, userId2);
        List<UserMessage> received = repo.findBySender_IdAndRecipient_IdOrderByCreatedAtAsc(userId2, userId1);

        return Stream.concat(sent.stream(), received.stream())
                .sorted(Comparator.comparing(UserMessage::getCreatedAt))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<MessageResponse> getThread(Long parentMessageId) {
        return repo.findByParentMessage_IdOrderByCreatedAtAsc(parentMessageId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private MessageResponse toDto(UserMessage m) {
        MessageResponse dto = new MessageResponse();
        dto.setId(m.getId());
        dto.setSenderId(m.getSender().getId());
        dto.setRecipientId(m.getRecipient().getId());
        dto.setContentText(m.getContentText());
        dto.setContentBase64(m.getContentBase64());
        dto.setRead(m.isRead());
        dto.setCreatedAt(m.getCreatedAt());
        dto.setReadAt(m.getReadAt());
        dto.setParentMessageId(
                m.getParentMessage() != null ? m.getParentMessage().getId() : null
        );
        return dto;
    }
}

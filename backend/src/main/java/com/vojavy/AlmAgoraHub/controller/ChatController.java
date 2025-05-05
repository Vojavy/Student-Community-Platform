// src/main/java/com/vojavy/AlmAgoraHub/controller/ChatController.java
package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.dto.requests.HistoryRequest;
import com.vojavy.AlmAgoraHub.dto.requests.SendMessageRequest;
import com.vojavy.AlmAgoraHub.dto.responses.MessageResponse;
import com.vojavy.AlmAgoraHub.service.user.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {

    private final UserMessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(UserMessageService messageService,
                          SimpMessagingTemplate messagingTemplate) {
        this.messageService    = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.history")
    public void fetchHistory(
            @Payload HistoryRequest req,
            Principal principal
    ) {
        Long me = Long.valueOf(principal.getName());
        List<MessageResponse> history =
                messageService.getConversation(me, req.getOtherUserId());

        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/history",
                history
        );
    }

    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload SendMessageRequest req,
            Principal principal
    ) {
        Long sender = Long.valueOf(principal.getName());
        MessageResponse resp = messageService.sendMessage(sender, req);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(resp.getRecipientId()),
                "/queue/messages",
                resp
        );
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/messages",
                resp
        );
    }

    @MessageMapping("/chat.read.{messageId}")
    public void readMessage(
            @DestinationVariable Long messageId,
            Principal principal
    ) {
        Long me = Long.valueOf(principal.getName());
        MessageResponse resp =
                messageService.markAsRead(messageId, me);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(resp.getSenderId()),
                "/queue/messages/read",
                resp
        );
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/messages/read",
                resp
        );
    }
}

package com.vojavy.AlmAgoraHub.model.user;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_messages")
public class UserMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_messages_id_seq")
    @SequenceGenerator(name = "user_messages_id_seq", sequenceName = "user_messages_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(columnDefinition = "TEXT")
    private String contentText;

    @Column(columnDefinition = "TEXT")
    private String contentBase64;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant readAt;

    @ManyToOne @JoinColumn(name = "parent_message_id")
    private UserMessage parentMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getContentBase64() {
        return contentBase64;
    }

    public void setContentBase64(String contentBase64) {
        this.contentBase64 = contentBase64;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public UserMessage getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(UserMessage parentMessage) {
        this.parentMessage = parentMessage;
    }
}

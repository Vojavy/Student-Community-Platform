package com.vojavy.AlmAgoraHub.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "user_friends")
@IdClass(UserFriend.UserFriendId.class)
public class UserFriend {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id_1", referencedColumnName = "id")
    private User user1;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id_2", referencedColumnName = "id")
    private User user2;

    @Column(nullable = false)
    private String status = "pending"; // 'pending', 'approved'

    @Column(nullable = false)
    private boolean hidden = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    // --- Getters and Setters ---

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // --- ID Class ---

    public static class UserFriendId implements Serializable {
        private Long user1;
        private Long user2;

        public UserFriendId() {}

        public UserFriendId(Long user1, Long user2) {
            this.user1 = user1;
            this.user2 = user2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserFriendId)) return false;
            UserFriendId that = (UserFriendId) o;
            return Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user1, user2);
        }
    }
}

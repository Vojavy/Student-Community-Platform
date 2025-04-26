package com.vojavy.AlmAgoraHub.model.group;

import com.vojavy.AlmAgoraHub.model.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "user_group_memberships")
public class GroupMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(nullable = false)
    private String status; // approved, pending, banned

    @Column(nullable = false)
    private String role;   // owner, admin, editor, member, invited, helper

    @Column(name = "joined_at")
    private Instant joinedAt = Instant.now();

    // --- Конструкторы ---
    public GroupMembership() {
    }

    public GroupMembership(User user, Group group, String role, String status, Instant joinedAt) {
        this.user = user;
        this.group = group;
        this.role = role;
        this.status = status;
        this.joinedAt = joinedAt;
    }

    // --- Getters / Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }
}
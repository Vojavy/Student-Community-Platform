package com.vojavy.AlmAgoraHub.model.group;

import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String topics;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = true;

    @Column(name = "min_role_for_posts", nullable = false)
    private String minRoleForPosts = "member";

    @Column(name = "min_role_for_events", nullable = false)
    private String minRoleForEvents = "admin";

    @ManyToOne
    @JoinColumn(name = "domain_id")
    private UniversityDomain domain;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupPost> posts;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupAnnouncement> announcements;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupAttachment> attachments;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMembership> memberships;

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTopics() {
        return topics;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getMinRoleForPosts() {
        return minRoleForPosts;
    }

    public String getMinRoleForEvents() {
        return minRoleForEvents;
    }

    public UniversityDomain getDomain() {
        return domain;
    }


    public List<GroupPost> getPosts() {
        return posts;
    }

    public List<GroupAnnouncement> getAnnouncements() {
        return announcements;
    }

    public List<GroupAttachment> getAttachments() {
        return attachments;
    }

    public List<GroupMembership> getMemberships() {
        return memberships;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public void setMinRoleForPosts(String minRoleForPosts) {
        this.minRoleForPosts = minRoleForPosts;
    }

    public void setMinRoleForEvents(String minRoleForEvents) {
        this.minRoleForEvents = minRoleForEvents;
    }

    public void setDomain(UniversityDomain domain) {
        this.domain = domain;
    }

    public void setPosts(List<GroupPost> posts) {
        this.posts = posts;
    }

    public void setAnnouncements(List<GroupAnnouncement> announcements) {
        this.announcements = announcements;
    }

    public void setAttachments(List<GroupAttachment> attachments) {
        this.attachments = attachments;
    }

    public void setMemberships(List<GroupMembership> memberships) {
        this.memberships = memberships;
    }
}

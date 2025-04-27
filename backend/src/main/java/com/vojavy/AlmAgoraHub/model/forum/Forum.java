// src/main/java/com/vojavy/AlmAgoraHub/model/forum/Forum.java
package com.vojavy.AlmAgoraHub.model.forum;

import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.model.user.Role;
import com.vojavy.AlmAgoraHub.model.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "forums")
public class Forum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 256)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String topics; // JSON array of strings

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "university_domain_id")
    private UniversityDomain universityDomain;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ForumStatus status = ForumStatus.active;

    @Column(name = "is_pinned", nullable = false)
    private boolean pinned = false;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = true;

    @Column(name = "is_closed", nullable = false)
    private boolean closed = false;

    // ← Заменили ElementCollection<RoleType> на обычную связь ManyToMany с Role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "forum_allowed_roles",
            joinColumns = @JoinColumn(name = "forum_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> allowedRoles = new HashSet<>();

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumPost> posts;

    // getters / setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTopics() { return topics; }
    public void setTopics(String topics) { this.topics = topics; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public UniversityDomain getUniversityDomain() { return universityDomain; }
    public void setUniversityDomain(UniversityDomain universityDomain) { this.universityDomain = universityDomain; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public ForumStatus getStatus() { return status; }
    public void setStatus(ForumStatus status) { this.status = status; }

    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public boolean isClosed() { return closed; }
    public void setClosed(boolean closed) { this.closed = closed; }

    public Set<Role> getAllowedRoles() { return allowedRoles; }
    public void setAllowedRoles(Set<Role> allowedRoles) { this.allowedRoles = allowedRoles; }

    public List<ForumPost> getPosts() { return posts; }
    public void setPosts(List<ForumPost> posts) { this.posts = posts; }
}

package com.vojavy.AlmAgoraHub.model.forum;

import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.user.Role;
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
    private String topics;     // JSON array of strings

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "university_domain_id", nullable = false)
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

    // allowed roles → Role
    @ManyToMany
    @JoinTable(name = "forum_allowed_roles",
            joinColumns = @JoinColumn(name = "forum_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> allowedRoles = new HashSet<>();

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumPost> posts;

    // getters + setters omitted for brevity
    // ...
}

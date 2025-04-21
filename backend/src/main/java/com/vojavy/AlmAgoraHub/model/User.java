package com.vojavy.AlmAgoraHub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "users")
public class User implements org.springframework.security.core.userdetails.UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authProvider;

    @Column(name = "provider_id", nullable = false)
    private Integer providerId;

    @Column(unique = true)
    private String email;

    @Column(length = 512)
    private String password;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "verification_code", length = 6)
    private String verificationCode;

    @Column(name = "verification_expires")
    private Instant verificationExpires;

    @Convert(converter = UserDetailsExtended.ConverterImpl.class)
    @JdbcTypeCode(SqlTypes.JSON)               // говорим Hibernate, что это JSON (_не_ varchar)
    @Column(columnDefinition = "jsonb")
    private UserDetailsExtended details;


    // --- Relations ---


    @ManyToOne
    @JoinColumn(name = "domain_id")
    private UniversityDomain domain;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserISData userDetails;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserToken> tokens;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<UserGroupMembership> groupMemberships;
//
//    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
//    private List<Event> events;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Calendar> calendars;
//
//    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
//    private List<SaleItem> saleItems;
//
//    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
//    private List<ForumPost> forumPosts;
//
//    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
//    private List<Forum> forums;
//
//    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
//    private List<GroupAnnouncement> announcements;
//
//    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL)
//    private List<GroupAttachment> attachments;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<GroupPost> groupPosts;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Log> logs;

    public User() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

// --- Getters and Setters omitted for brevity ---


    public Long getId() {
        return id;
    }

    public UniversityDomain getDomain() {
        return domain;
    }

    public void setDomain(UniversityDomain domain) {
        this.domain = domain;
    }

    public void setUserDetails(UserISData userDetails) {
        this.userDetails = userDetails;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return org.springframework.security.core.userdetails.UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return org.springframework.security.core.userdetails.UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return org.springframework.security.core.userdetails.UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public UserISData getUserDetails() {
        return userDetails;
    }

    public List<UserToken> getTokens() {
        return tokens;
    }

    public void setTokens(List<UserToken> tokens) {
        this.tokens = tokens;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getVerificationExpires() {
        return verificationExpires;
    }

    public void setVerificationExpires(Instant verificationExpires) {
        this.verificationExpires = verificationExpires;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public void setId(Long id) { this.id = id; }

    public UserDetailsExtended getDetails() {
        return details;
    }

    public void setDetails(UserDetailsExtended details) {
        this.details = details;
    }
}

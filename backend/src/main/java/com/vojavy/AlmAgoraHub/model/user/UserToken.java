package com.vojavy.AlmAgoraHub.model.user;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_tokens")
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String token;

    @Column(name = "token_type", nullable = false)
    private String tokenType; // 'auth', 'refresh', 'verify'

    @Column(nullable = false)
    private Instant expiration;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "token_origin", nullable = false)
    private String tokenOrigin = "local";

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public void setExpiration(Instant expiration) {
        this.expiration = expiration;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenOrigin() {
        return tokenOrigin;
    }

    public void setTokenOrigin(String tokenOrigin) {
        this.tokenOrigin = tokenOrigin;
    }

    public void setUserId(Long userId) {
        if (this.user == null)
            this.user = new User();
        this.user.setId(userId);
    }
}

package com.vojavy.AlmAgoraHub.model.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name; // здесь теперь RoleType, не String

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role() {}

    public Role(RoleType name) {
        this.name = name;
    }

    public Role(String name) {
        this.name = RoleType.fromString(name);
    }

    public Long getId() {
        return id;
    }

    public RoleType getName() {
        return name;
    }

    public void setName(RoleType name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    @Override
    public String getAuthority() {
        return name.name(); // Spring Security требует строку
    }
}

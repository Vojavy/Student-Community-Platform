package com.vojavy.AlmAgoraHub.model.user;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {
    ROLE_UNVERIFIED,
    ROLE_USER,
    ROLE_STUDENT,
    ROLE_TEACHER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }

    public static RoleType fromString(String roleName) {
        for (RoleType role : RoleType.values()) {
            if (role.name().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No RoleType with name " + roleName);
    }
}

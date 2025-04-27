package com.vojavy.AlmAgoraHub.dto.responses;

import com.vojavy.AlmAgoraHub.model.user.RoleType;

public class RoleResponse {
    private final Long id;
    private final String name;

    public RoleResponse(
            Long id,
            String name
    ) {
        this.id = id;
        this.name = name;
    }

    public static RoleResponse fromEnum(Long id, RoleType role) {
        return new RoleResponse(id, role.name());
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}

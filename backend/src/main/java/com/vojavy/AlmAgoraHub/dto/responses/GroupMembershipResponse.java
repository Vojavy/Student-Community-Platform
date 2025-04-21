package com.vojavy.AlmAgoraHub.dto.responses;

import com.vojavy.AlmAgoraHub.model.group.GroupMembership;

public class GroupMembershipResponse {
    private Long userId;
    private String role;
    private String status;
    private String email;

    public static GroupMembershipResponse fromEntity(GroupMembership membership) {
        GroupMembershipResponse dto = new GroupMembershipResponse();
        dto.userId = membership.getUser().getId();
        dto.role = membership.getRole();
        dto.status = membership.getStatus();
        dto.email = membership.getUser().getEmail();
        return dto;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }
}

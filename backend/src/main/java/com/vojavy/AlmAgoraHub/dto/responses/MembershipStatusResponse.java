package com.vojavy.AlmAgoraHub.dto.responses;

public class MembershipStatusResponse {
    private String status;
    private String role;

    public MembershipStatusResponse() { }

    public MembershipStatusResponse(String status, String role) {
        this.status = status;
        this.role   = role;
    }

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

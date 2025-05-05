package com.vojavy.AlmAgoraHub.dto.requests;

public class HistoryRequest {
    private Long otherUserId;

    public HistoryRequest() {}
    public HistoryRequest(Long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public Long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(Long otherUserId) {
        this.otherUserId = otherUserId;
    }
}

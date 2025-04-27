// src/main/java/com/vojavy/AlmAgoraHub/dto/requests/CreateForumPostRequest.java
package com.vojavy.AlmAgoraHub.dto.requests;

import jakarta.validation.constraints.NotBlank;

public class CreateForumPostRequest {
    @NotBlank
    private String content;

    private Integer parentPostId;

    public CreateForumPostRequest() {}

    public CreateForumPostRequest(String content, Integer parentPostId) {
        this.content      = content;
        this.parentPostId = parentPostId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getParentPostId() {
        return parentPostId;
    }

    public void setParentPostId(Integer parentPostId) {
        this.parentPostId = parentPostId;
    }
}

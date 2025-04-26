package com.vojavy.AlmAgoraHub.dto.requests;

import java.util.List;

public class GroupPostRequest {
    private String title;
    private String content;
    private String topics;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTopics() { return topics; }
    public void setTopics(String topics) { this.topics = topics; }
}

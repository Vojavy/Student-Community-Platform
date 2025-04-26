package com.vojavy.AlmAgoraHub.dto.requests;

import java.util.List;

public class GroupSettingsRequest {
    private String name;
    private String description;
    private boolean isPublic;
    private String minRoleForPosts;
    private String minRoleForEvents;
    private String domain;
    private String topics;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getMinRoleForPosts() {
        return minRoleForPosts;
    }

    public void setMinRoleForPosts(String minRoleForPosts) {
        this.minRoleForPosts = minRoleForPosts;
    }

    public String getMinRoleForEvents() {
        return minRoleForEvents;
    }

    public void setMinRoleForEvents(String minRoleForEvents) {
        this.minRoleForEvents = minRoleForEvents;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }
}

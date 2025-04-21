package com.vojavy.AlmAgoraHub.dto.requests;

public class CreateGroupRequest {
    private String name;
    private String description;
    private String topics; // JSON строка вида {"key":"value"}
    private boolean isPublic = true;
    private String minRoleForPosts = "member";
    private String minRoleForEvents = "admin";
    private String domain; // может быть null

    // --- Getters and Setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTopics() { return topics; }
    public void setTopics(String topics) { this.topics = topics; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public String getMinRoleForPosts() { return minRoleForPosts; }
    public void setMinRoleForPosts(String minRoleForPosts) { this.minRoleForPosts = minRoleForPosts; }

    public String getMinRoleForEvents() { return minRoleForEvents; }
    public void setMinRoleForEvents(String minRoleForEvents) { this.minRoleForEvents = minRoleForEvents; }

    public String getDomain() { return domain; }
    public void setDomainId(String domain) { this.domain = domain; }
}

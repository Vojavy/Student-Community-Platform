// src/main/java/com/vojavy/AlmAgoraHub/dto/requests/CreateForumRequest.java
package com.vojavy.AlmAgoraHub.dto.requests;

import com.vojavy.AlmAgoraHub.model.forum.ForumStatus;
import com.vojavy.AlmAgoraHub.model.user.RoleType;

import java.util.List;

public class CreateForumRequest {
    private String name;
    private List<String> topics;
    private String description;
    private String universityDomain;
    private ForumStatus status;
    private boolean pinned;
    private boolean isPublic;
    private boolean closed;
    private List<String> allowedRoles;

    // getters & setters omitted for brevity
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getTopics() { return topics; }
    public void setTopics(List<String> topics) { this.topics = topics; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUniversityDomain() { return universityDomain; }
    public void setUniversityDomainId(String universityDomain) {
        this.universityDomain = universityDomain;
    }

    public ForumStatus getStatus() { return status; }
    public void setStatus(ForumStatus status) { this.status = status; }

    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public boolean isClosed() { return closed; }
    public void setClosed(boolean closed) { this.closed = closed; }

    public List<String> getAllowedRoles() {return allowedRoles;}
    public void setAllowedRoles(List<String> allowedRole) {this.allowedRoles = allowedRole;}
}

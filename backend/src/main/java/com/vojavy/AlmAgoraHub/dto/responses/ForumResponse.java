// ForumResponse.java
package com.vojavy.AlmAgoraHub.dto.responses;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.model.forum.Forum;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Lightweight DTO for forum listings (/forums GET).
 */
public class ForumResponse {

    private Long id;
    private String name;
    private List<String> topics;
    private String description;
    private UniversityDomain universityDomain;
    private boolean isPublic;
    private boolean pinned;
    private UserSummaryResponse createdBy;
    private String status;
    private Instant createdAt;


    public static ForumResponse fromEntity(Forum forum) {
        ForumResponse r = new ForumResponse();
        r.id                 = forum.getId().longValue();
        r.name               = forum.getName();
        r.topics             = JsonUtil.splitArray(forum.getTopics());
        r.description        = forum.getDescription();
        r.universityDomain   = forum.getUniversityDomain();
        r.pinned             = forum.isPinned();
        r.isPublic           = forum.isPublic();
        r.status             = forum.getStatus().name();
        r.createdAt          = forum.getCreatedAt();
        r.createdBy          = new UserSummaryResponse(
                forum.getCreatedBy().getId(),
                null,
                forum.getCreatedBy().getUsername()
        );
        return r;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public UniversityDomain getUniversityDomain() {
        return universityDomain;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPinned() {
        return pinned;
    }

    public UserSummaryResponse getCreatedBy() {
        return createdBy;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    private static final class JsonUtil {
        private static final ObjectMapper om = new ObjectMapper();
        static List<String> splitArray(String raw) {
            if (raw == null || raw.isBlank()) return Collections.emptyList();
            try {
                return om.readValue(raw, new TypeReference<>() {});
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }
    }
}

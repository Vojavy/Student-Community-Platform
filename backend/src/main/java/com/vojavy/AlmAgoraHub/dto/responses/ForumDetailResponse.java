// ForumDetailResponse.java
package com.vojavy.AlmAgoraHub.dto.responses;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.model.forum.Forum;
import com.vojavy.AlmAgoraHub.model.user.Role;
import com.vojavy.AlmAgoraHub.model.user.RoleType;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ForumDetailResponse {

    private Long id;
    private String name;
    private List<String> topics;
    private String description;
    private String status;
    private boolean pinned;
    private boolean isPublic;
    private boolean closed;
    private UniversityDomain universityDomain;
    private Instant createdAt;
    private UserSummaryResponse createdBy;
    private List<String> allowedRoles;

    public static ForumDetailResponse fromEntity(Forum forum) {
        ForumDetailResponse r = new ForumDetailResponse();
        r.id                 = forum.getId().longValue();
        r.name               = forum.getName();
        r.topics             = JsonUtil.splitArray(forum.getTopics());
        r.description        = forum.getDescription();
        r.status             = forum.getStatus().name();
        r.pinned             = forum.isPinned();
        r.isPublic           = forum.isPublic();
        r.closed             = forum.isClosed();
        r.universityDomain   = forum.getUniversityDomain();
        r.createdAt          = forum.getCreatedAt();
        r.createdBy          = new UserSummaryResponse(
                forum.getCreatedBy().getId(),
                null,
                forum.getCreatedBy().getUsername()
        );
        // map the enum set to their names:
        r.allowedRoles       = forum.getAllowedRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
        return r;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public boolean isPinned() {
        return pinned;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isClosed() {
        return closed;
    }

    public UniversityDomain getUniversityDomain() {
        return universityDomain;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UserSummaryResponse getCreatedBy() {
        return createdBy;
    }

    public List<String> getAllowedRoles() {
        return allowedRoles;
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

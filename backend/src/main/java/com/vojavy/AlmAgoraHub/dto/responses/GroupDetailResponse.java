// com.vojavy.AlmAgoraHub.dto.responses.GroupDetailResponse.java
package com.vojavy.AlmAgoraHub.dto.responses;

import com.vojavy.AlmAgoraHub.model.UniversityDomain;

import java.time.Instant;
import java.util.List;

public record GroupDetailResponse(
        Long id,
        String name,
        String description,
        List<String> topics,
        Boolean isPublic,
        String minRoleForPosts,
        String minRoleForEvents,
        UniversityDomain domain,
        Instant createdAt,
        UserSummaryResponse owner,
        List<UserSummaryResponse> admins,
        List<UserSummaryResponse> helpers,
        List<UserSummaryResponse> members
) {}

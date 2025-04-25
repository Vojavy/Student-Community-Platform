package com.vojavy.AlmAgoraHub.dto.responses;

import java.time.Instant;

public record GroupMembershipResponse(
        Long id,
        UserSummaryResponse user,
        String role,
        String status,
        Instant joinedAt
) {}

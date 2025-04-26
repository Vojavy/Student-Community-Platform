package com.vojavy.AlmAgoraHub.dto.responses;

import com.vojavy.AlmAgoraHub.model.group.GroupPost;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class GroupPostResponse {
    private Long id;
    private String title;
    private String content;
    private List<String> topics;
    private UserSummaryResponse author;
    private Instant createdAt;
    private Instant updatedAt;


    public static GroupPostResponse fromEntity(GroupPost p) {
        GroupPostResponse r = new GroupPostResponse();
        r.id        = p.getId();
        r.title     = p.getTitle();
        r.content   = p.getContent();
        r.topics    = Collections.singletonList(p.getTopics());
        r.createdAt = p.getCreatedAt();
        r.updatedAt = p.getUpdatedAt();
        r.author    = new UserSummaryResponse(
                p.getUser().getId(),
                null,
                p.getUser().getUsername()
        );
        return r;
    }

    public Long getId()                    { return id; }
    public String getTitle()               { return title; }
    public String getContent()             { return content; }
    public List<String> getTopics()        { return topics; }
    public UserSummaryResponse getAuthor() { return author; }
    public Instant getCreatedAt()          { return createdAt; }
    public Instant getUpdatedAt()          { return updatedAt; }
    public void setAuthor(UserSummaryResponse author) {
        this.author = author;
    }
}

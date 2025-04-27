// ForumPostResponse.java
package com.vojavy.AlmAgoraHub.dto.responses;

import com.vojavy.AlmAgoraHub.model.forum.ForumPost;

import java.time.Instant;

/**
 * Single forum-post DTO.
 */
public class ForumPostResponse {

    private Long   id;
    private String content;
    private Long   parentPostId;
    private UserSummaryResponse author;
    private Instant createdAt;
    private Instant updatedAt;

    public static ForumPostResponse fromEntity(ForumPost forumPost) {
        ForumPostResponse r = new ForumPostResponse();
        r.id          = forumPost.getId().longValue();
        r.content     = forumPost.getContent();
        r.parentPostId= forumPost.getParentPost() == null ? null
                : forumPost.getParentPost().getId().longValue();
        r.createdAt   = forumPost.getCreatedAt();
        r.updatedAt   = forumPost.getUpdatedAt();
        r.author      = new UserSummaryResponse(
                forumPost.getAuthor().getId(),
                null,
                forumPost.getAuthor().getUsername()
        );
        return r;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentPostId() {
        return parentPostId;
    }

    public void setParentPostId(Long parentPostId) {
        this.parentPostId = parentPostId;
    }

    public UserSummaryResponse getAuthor() {
        return author;
    }

    public void setAuthor(UserSummaryResponse author) {
        this.author = author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

// src/main/java/com/vojavy/AlmAgoraHub/controller/ForumController.java
package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.dto.requests.CreateForumPostRequest;
import com.vojavy.AlmAgoraHub.dto.requests.CreateForumRequest;
import com.vojavy.AlmAgoraHub.dto.responses.ForumDetailResponse;
import com.vojavy.AlmAgoraHub.dto.responses.ForumPostResponse;
import com.vojavy.AlmAgoraHub.dto.responses.ForumResponse;
import com.vojavy.AlmAgoraHub.model.forum.ForumPost;
import com.vojavy.AlmAgoraHub.model.forum.ForumStatus;
import com.vojavy.AlmAgoraHub.service.user.UserService;
import com.vojavy.AlmAgoraHub.service.forum.ForumService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/forums")
public class ForumController {

    private final ForumService forumService;
    private final UserService  userService;

    public ForumController(ForumService forumService,
                           UserService userService) {
        this.forumService = forumService;
        this.userService  = userService;
    }

    //=====================================
    // Forum management
    //=====================================

    @PostMapping
    public ResponseEntity<ForumResponse> createForum(
            @Valid @RequestBody CreateForumRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long creatorId = userService.extractUserId(authHeader);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ForumResponse.fromEntity(
                        forumService.createForum(req, creatorId)
                ));
    }

    @GetMapping
    public Page<ForumResponse> listForums(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0")      int page,
            @RequestParam(defaultValue = "25")     int size,
            @RequestParam(required = false)        Boolean isPublic,
            @RequestParam(required = false)        String domain,
            @RequestParam(required = false)        ForumStatus status,
            @RequestParam(required = false)        String name,
            @RequestParam(required = false)        List<String> topics,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        Long userId = userService.extractUserId(authHeader);
        return forumService
                .browseForums(userId, isPublic, domain, status, name, topics, sort, page, size)
                .map(ForumResponse::fromEntity);
    }


    @GetMapping("/{forumId}")
    public ResponseEntity<ForumDetailResponse> getForumDetails(
            @PathVariable Integer forumId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        ForumDetailResponse detail = forumService.getForumDetails(forumId, userId);
        return ResponseEntity.ok(detail);
    }

    //=====================================
    // Forum posts
    //=====================================

    @PostMapping("/{forumId}/posts")
    public ResponseEntity<ForumPostResponse> createPost(
            @PathVariable Integer forumId,
            @Valid @RequestBody CreateForumPostRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        ForumPost created = forumService.createPost(userId, forumId, req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ForumPostResponse.fromEntity(created));
    }

    @GetMapping("/{forumId}/posts")
    public Page<ForumPostResponse> listPosts(
            @PathVariable Integer forumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer parentPostId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        Page<ForumPost> postsPage = forumService.listPosts(forumId, userId, parentPostId, page, size);
        List<ForumPostResponse> content = postsPage
                .stream()
                .map(ForumPostResponse::fromEntity)
                .toList();
        return new PageImpl<>(content, postsPage.getPageable(), postsPage.getTotalElements());
    }

    // optionally: edit or delete posts, etc.
}

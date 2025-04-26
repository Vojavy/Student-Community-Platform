package com.vojavy.AlmAgoraHub.service.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vojavy.AlmAgoraHub.dto.requests.GroupPostRequest;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.group.GroupPost;
import com.vojavy.AlmAgoraHub.repository.group.GroupPostRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class GroupPostService {

    private final GroupPostRepository postRepository;
    private final ObjectMapper objectMapper;

    public GroupPostService(GroupPostRepository postRepository) {
        this.postRepository = postRepository;
        this.objectMapper = new ObjectMapper();
    }

    // =====================
    // Post List & Search
    // =====================
    protected Page<GroupPost> getPostsForGroup(Group group, int page, int size, String search) {
        List<GroupPost> all = postRepository.findByGroup(group);

        if (search != null && !search.isBlank()) {
            all = all.stream()
                    .filter(p -> (p.getTitle() != null && p.getTitle().toLowerCase().contains(search.toLowerCase()))
                            || (p.getContent() != null && p.getContent().toLowerCase().contains(search.toLowerCase())))
                    .collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        int start = (int) Math.min(pageable.getOffset(), all.size());
        int end   = Math.min(start + pageable.getPageSize(), all.size());

        return new PageImpl<>(all.subList(start, end), pageable, all.size());
    }

    // =====================
    // Get One Post
    // =====================
    protected GroupPost getPost(Group group, Long postId) {
        return postRepository.findById(Math.toIntExact(postId))
                .filter(p -> p.getGroup().getId().equals(group.getId()))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Post not found"));
    }

    // =====================
    // Create Post
    // =====================
    protected GroupPost createPost(Group group, User user, GroupPostRequest req) {
        GroupPost post = new GroupPost();
        post.setGroup(group);
        post.setUser(user);
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setTopics(req.getTopics());
        post.setCreatedAt(Instant.now());

        return postRepository.save(post);
    }

    // =====================
    // Update Post
    // =====================
    protected GroupPost updatePost(GroupPost existing, GroupPostRequest req) {
        existing.setTitle(req.getTitle());
        existing.setContent(req.getContent());
        existing.setTopics(req.getTopics());
        existing.setUpdatedAt(Instant.now());

        return postRepository.save(existing);
    }

    // =====================
    // Delete Post
    // =====================
    protected void deletePost(Long postId) {
        postRepository.deleteById(Math.toIntExact(postId));
    }
}

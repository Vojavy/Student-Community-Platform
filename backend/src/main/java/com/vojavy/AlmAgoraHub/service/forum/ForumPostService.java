// src/main/java/com/vojavy/AlmAgoraHub/service/forum/ForumPostService.java
package com.vojavy.AlmAgoraHub.service.forum;

import com.vojavy.AlmAgoraHub.model.forum.Forum;
import com.vojavy.AlmAgoraHub.model.forum.ForumPost;
import com.vojavy.AlmAgoraHub.repository.forum.ForumPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class ForumPostService {

    private final ForumPostRepository postRepository;

    public ForumPostService(ForumPostRepository postRepository) {
        this.postRepository = postRepository;
    }

    protected ForumPost createPost(
            Forum forum,
            com.vojavy.AlmAgoraHub.model.user.User author,
            String content,
            ForumPost parent
    ) {
        ForumPost p = new ForumPost();
        p.setForum(forum);
        p.setAuthor(author);
        p.setContent(content);
        p.setCreatedAt(Instant.now());
        p.setParentPost(parent);
        return postRepository.save(p);
    }

    protected ForumPost getPost(Forum forum, Integer postId) {
        return postRepository.findById(postId)
                .filter(p -> p.getForum().getId().equals(forum.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Parent post not found"));
    }

    public List<ForumPost> findAllByForum(Forum forum) {
        return postRepository.findByForumOrderByCreatedAtAsc(forum);
    }
}

package com.vojavy.AlmAgoraHub.service.forum;

import com.vojavy.AlmAgoraHub.model.forum.Forum;
import com.vojavy.AlmAgoraHub.model.forum.ForumPost;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.repository.forum.ForumPostRepository;
import com.vojavy.AlmAgoraHub.service.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
class ForumPostService {

    private final ForumPostRepository postRepository;
    private final NotificationService notificationService;

    private final int MILESTONE = 10;

    protected ForumPostService(
            ForumPostRepository postRepository,
            NotificationService notificationService
    ) {
        this.postRepository = postRepository;
        this.notificationService = notificationService;
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
        ForumPost saved = postRepository.save(p);
        long total = postRepository.countByForum(forum);
        if (total % MILESTONE == 0) {
            List<User> subs = findAllByForum(forum)
                    .stream()
                    .map(ForumPost::getAuthor)
                    .distinct()
                    .toList();
            notificationService.sendForumPostMilestoneNotification(forum, (int) total, subs);
        }
        return saved;
    }

    protected ForumPost getPost(Forum forum, Integer postId) {
        return postRepository.findById(postId)
                .filter(p -> p.getForum().getId().equals(forum.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Parent post not found"));
    }

    protected List<ForumPost> findAllByForum(Forum forum) {
        return postRepository.findByForumOrderByCreatedAtAsc(forum);
    }
}

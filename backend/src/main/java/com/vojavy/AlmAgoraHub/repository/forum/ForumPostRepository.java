package com.vojavy.AlmAgoraHub.repository.forum;

import com.vojavy.AlmAgoraHub.model.forum.Forum;
import com.vojavy.AlmAgoraHub.model.forum.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Integer> {
    List<ForumPost> findByForumOrderByCreatedAtAsc(Forum forum);

    Long countByForum(Forum forum);
}

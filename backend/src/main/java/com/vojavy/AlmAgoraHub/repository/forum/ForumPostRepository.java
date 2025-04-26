package com.vojavy.AlmAgoraHub.repository.forum;

import com.vojavy.AlmAgoraHub.model.forum.Forum;
import com.vojavy.AlmAgoraHub.model.forum.ForumPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Integer> {
    Page<ForumPost> findByForum(Forum forum, Pageable pageable);
}

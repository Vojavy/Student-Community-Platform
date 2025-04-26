package com.vojavy.AlmAgoraHub.repository.forum;

import com.vojavy.AlmAgoraHub.model.forum.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Integer> {
}

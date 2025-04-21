package com.vojavy.AlmAgoraHub.service.group;

import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.group.GroupPost;
import com.vojavy.AlmAgoraHub.repository.group.GroupPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupPostService {

    private final GroupPostRepository postRepository;

    public GroupPostService(GroupPostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<GroupPost> getPostsForGroup(Group group) {
        return postRepository.findByGroup(group);
    }

    public Optional<GroupPost> getPostById(int id) {
        return postRepository.findById(id);
    }

    public GroupPost createPost(GroupPost post) {
        return postRepository.save(post);
    }

    public GroupPost updatePost(GroupPost post) {
        return postRepository.save(post);
    }

    public void deletePost(int id) {
        postRepository.deleteById(id);
    }
}

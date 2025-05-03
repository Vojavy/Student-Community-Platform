package com.vojavy.AlmAgoraHub.service.notification;

import com.vojavy.AlmAgoraHub.model.forum.Forum;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.group.Group;

import java.util.List;

public interface NotificationService {

    void sendFriendRequestNotification(User from, User to);

    void sendGroupInviteNotification(User inviter, User target, Group group, String role);
    void sendGroupJoinApprovalNotification(User approver, User target, Group group, String newRole);

    void sendGroupJoinAcceptedNotification(User user, Group group);
    void sendForumPostMilestoneNotification(Forum forum, int totalPosts, List<User> recipients);
}

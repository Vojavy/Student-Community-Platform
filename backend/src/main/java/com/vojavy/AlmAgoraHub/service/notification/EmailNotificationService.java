package com.vojavy.AlmAgoraHub.service.notification;

import com.vojavy.AlmAgoraHub.model.forum.Forum;
import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailNotificationService implements NotificationService {

    private final EmailService emailService;

    @Value("${hosting.address.frontend}")
    private String frontendBaseUrl;

    public EmailNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    @Async("notificationExecutor")
    public void sendFriendRequestNotification(User from, User to) {
        String subject = "👋 New friend request from " + from.getUsername();
        String url     = frontendBaseUrl + "/user/" + from.getId();
        String html = """
            <p>Hi %s,</p>
            <p><strong>%s</strong> has sent you a friend request.</p>
            <p><a href="%s">View their profile</a></p>
            """.formatted(to.getUsername(), from.getUsername(), url);
        try {
            emailService.sendHtmlEmail(to.getEmail(), subject, html);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async("notificationExecutor")
    public void sendGroupInviteNotification(User inviter, User target, Group group, String role) {
        String subject = "📬 You've been invited to the group \"" + group.getName() + "\"";
        String url     = frontendBaseUrl + "/app/groups/" + group.getId();
        String html = """
            <p>Hello %s,</p>
            <p><strong>%s</strong> invited you to join the group <strong>%s</strong> as <em>%s</em>.</p>
            <p><a href="%s">View group</a></p>
            """.formatted(
                target.getUsername(),
                inviter.getUsername(),
                group.getName(),
                role,
                url
        );
        try {
            emailService.sendHtmlEmail(target.getEmail(), subject, html);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async("notificationExecutor")
    public void sendGroupJoinApprovalNotification(User approver, User target, Group group, String newRole) {
        String subject = "✅ Your membership in \"" + group.getName() + "\" was approved";
        String url     = frontendBaseUrl + "/app/groups/" + group.getId();
        String html = """
            <p>Hi %s,</p>
            <p>Your request to join <strong>%s</strong> has been approved by %s. Your role: <strong>%s</strong>.</p>
            <p><a href="%s">Go to group</a></p>
            """.formatted(
                target.getUsername(),
                group.getName(),
                approver.getUsername(),
                newRole,
                url
        );
        try {
            emailService.sendHtmlEmail(target.getEmail(), subject, html);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async("notificationExecutor")
    public void sendForumPostMilestoneNotification(Forum forum, int totalPosts, List<User> recipients) {
        String subject  = "🎉 Forum \"" + forum.getName() + "\" reached " + totalPosts + " posts!";
        String url      = frontendBaseUrl + "/app/forum/" + forum.getId();
        String html = """
            <p>Hello,</p>
            <p>The forum <strong>%s</strong> has just hit <strong>%d</strong> posts.</p>
            <p><a href="%s">Check it out</a></p>
            """.formatted(
                forum.getName(),
                totalPosts,
                url
        );

        for (User u : recipients) {
            try {
                emailService.sendHtmlEmail(u.getEmail(), subject, html);
            } catch (MessagingException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    @Async("notificationExecutor")
    public void sendGroupJoinAcceptedNotification(User user, Group group) {
        String subject   = "Your request to join \"" + group.getName() + "\" was accepted";
        String groupUrl  = frontendBaseUrl + "/app/groups/" + group.getId();
        String html = """
            <p>Hi %s,</p>
            <p>Your request to join the group <strong>%s</strong> has been accepted.</p>
            <p><a href="%s">Go to the group</a></p>
            """.formatted(
                user.getUsername(),
                group.getName(),
                groupUrl
        );
        try {
            emailService.sendHtmlEmail(user.getEmail(), subject, html);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

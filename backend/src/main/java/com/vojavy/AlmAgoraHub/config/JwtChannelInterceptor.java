// src/main/java/com/vojavy/AlmAgoraHub/config/JwtChannelInterceptor.java
package com.vojavy.AlmAgoraHub.config;

import com.vojavy.AlmAgoraHub.service.authentication.JwtService;
import com.vojavy.AlmAgoraHub.service.authentication.UserTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Principal;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserTokenService userTokenService;

    public JwtChannelInterceptor(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            UserTokenService userTokenService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userTokenService = userTokenService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, StompHeaderAccessor.class
        );
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
            if (!StringUtils.hasText(auth) || !auth.startsWith("Bearer ")) {
                throw new AccessDeniedException("Missing or invalid Authorization header");
            }

            String token = auth.substring(7).trim();

            if (userTokenService.getUserTokenByToken(token).isEmpty()) {
                throw new AccessDeniedException("JWT token not found");
            }

            Long userId = jwtService.extractClaim(token, claims -> claims.get("id", Long.class));
            if (userId == null) {
                throw new AccessDeniedException("Cannot extract userId from JWT");
            }

            var ud = userDetailsService.loadUserByUsername(jwtService.extractUsername(token));
            if (!jwtService.validateToken(token, ud)) {
                throw new AccessDeniedException("JWT validation failed");
            }

            Authentication authObj = new UsernamePasswordAuthenticationToken(
                    ud, null, ud.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authObj);

            Principal principal = () -> userId.toString();
            accessor.setUser(principal);
        }
        return message;
    }
}

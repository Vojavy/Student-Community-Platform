package com.vojavy.AlmAgoraHub.config;

import com.vojavy.AlmAgoraHub.repository.UserTokenRepository;
import com.vojavy.AlmAgoraHub.service.JwtService;
import com.vojavy.AlmAgoraHub.service.UserTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserTokenService userTokenService;

    public JwtAuthFilter(
            HandlerExceptionResolver handlerExceptionResolver,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            UserTokenService userTokenService
    ) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userTokenService = userTokenService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = null;

        try {
            token = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(token);

            boolean tokenExists = userTokenService.getUserTokenByToken(token).isPresent();
            if (!tokenExists) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token: not found in storage");
                return;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            chain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            System.out.println("❌ Token expired: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT expired");
            if (token != null) {
                userTokenService.deleteToken(token);
            }

        } catch (Exception e) {
            System.out.println("❌ Other JWT error: " + e);
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}

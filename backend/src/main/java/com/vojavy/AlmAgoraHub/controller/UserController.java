package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.dto.requests.UpdateUserRequest;
import com.vojavy.AlmAgoraHub.dto.responses.RoleResponse;
import com.vojavy.AlmAgoraHub.dto.responses.UserProfileResponse;
import com.vojavy.AlmAgoraHub.model.user.RoleType;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.user.UserDetailsExtended;
import com.vojavy.AlmAgoraHub.service.authentication.JwtService;
import com.vojavy.AlmAgoraHub.service.user.UserProfileService;
import com.vojavy.AlmAgoraHub.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserProfileService userProfileService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(
            UserService userService,
            JwtService jwtService,
            BCryptPasswordEncoder passwordEncoder,
            UserProfileService userProfileService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userProfileService = userProfileService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestBody UpdateUserRequest request,
                                        @RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");

        if (!isAuthorized(id, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        try {
            userService.updateUserWithPasswordCheck(id, request, passwordEncoder);
            return ResponseEntity.ok("User updated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PutMapping("/{id}/details")
    public ResponseEntity<?> updateUserDetails(@PathVariable Long id,
                                               @RequestBody UserDetailsExtended details,
                                               @RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");

        if (!isAuthorized(id, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        try {
            userService.updateUserDetails(id, details);
            return ResponseEntity.ok("Details updated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponse> getProfileById(@PathVariable Long id) {
        Optional<UserProfileResponse> response = userProfileService.getProfileById(id);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getMyRoles(
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = userService.extractUserId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return userService.findById(userId)
                .map(user -> user.getRoles().stream()
                        .map(role -> RoleResponse.fromEnum(role.getId(), RoleType.fromString(role.getName().name())))
                        .toList()
                )
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }



    private boolean isAuthorized(Long pathUserId, String token) {
        Long tokenUserId = userService.extractUserId(token);
        String roles = jwtService.extractClaim(token, claims -> claims.get("roles", String.class));
        boolean isAdmin = roles != null && roles.contains("ADMIN");
        return pathUserId.equals(tokenUserId) || isAdmin;
    }
}

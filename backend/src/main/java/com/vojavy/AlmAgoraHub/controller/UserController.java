package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.dto.requests.UpdateUserRequest;
import com.vojavy.AlmAgoraHub.dto.responses.UserProfileResponse;
import com.vojavy.AlmAgoraHub.model.user.UserDetailsExtended;
import com.vojavy.AlmAgoraHub.service.authentication.JwtService;
import com.vojavy.AlmAgoraHub.service.User.UserProfileService;
import com.vojavy.AlmAgoraHub.service.User.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    //TODO Приотправке он почему то сохраняет лишь часть данных
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

    private boolean isAuthorized(Long pathUserId, String token) {
        Long tokenUserId = userService.extractUserId(token);
        String roles = jwtService.extractClaim(token, claims -> claims.get("roles", String.class));
        boolean isAdmin = roles != null && roles.contains("ADMIN");
        return pathUserId.equals(tokenUserId) || isAdmin;
    }
}

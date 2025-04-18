// src/main/java/com/vojavy/AlmAgoraHub/controller/StagController.java
package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import com.vojavy.AlmAgoraHub.service.StagService;
import com.vojavy.AlmAgoraHub.service.JwtService;
import com.vojavy.AlmAgoraHub.model.User;
import com.vojavy.AlmAgoraHub.repository.UserRepository;
import com.vojavy.AlmAgoraHub.responses.StudentInfoResponseDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/stag")
public class StagController {

    private final StagService stagService;
    private final UserService userService;

    @Autowired
    public StagController(StagService stagService,
                          UserService userService) {
        this.stagService = stagService;
        this.userService = userService;
    }

    @PostMapping("/ticket")
    public ResponseEntity<String> saveStagTokenAndUserData(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String ticket = payload.get("stagUserTicket");
        String longTicket = payload.get("longTicket");
        String domain = payload.get("domain");

        if (ticket == null || ticket.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing stagUserTicket");
        }

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        stagService.saveTokenAndFetchUserData(user, ticket, longTicket, domain);
        return ResponseEntity.ok("STAG token and data saved");
    }

    @DeleteMapping("/ticket")
    public ResponseEntity<String> deleteStagTokenManually(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        stagService.deleteStagToken(user.getId());
        return ResponseEntity.ok("STAG token deleted");
    }

    /**
     * Test endpoint: fetch the STAG student info for the currently logged-in user.
     */
    @GetMapping("/student")
    public ResponseEntity<StudentInfoResponseDto> getStudent(
            @RequestParam String domain,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        String osCislo = user.getProviderId().toString();
        StudentInfoResponseDto info = stagService.fetchStudentInfo(user.getId(), osCislo, domain);
        return ResponseEntity.ok(info);
    }
}

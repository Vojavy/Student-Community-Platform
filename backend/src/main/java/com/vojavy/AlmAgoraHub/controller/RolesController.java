package com.vojavy.AlmAgoraHub.controller;


import com.vojavy.AlmAgoraHub.dto.responses.RoleResponse;
import com.vojavy.AlmAgoraHub.model.user.Role;
import com.vojavy.AlmAgoraHub.service.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolesController {

    private final RoleService roleService;

    @Autowired
    public RolesController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getRoles() {
        return ResponseEntity.ok().body(
                roleService
                        .getAll()
                        .stream()
                        .map(
                                role -> (
                                        new RoleResponse(role.getId(), role.getName().name())
                                )
                        )
                        .toList());
    }
}

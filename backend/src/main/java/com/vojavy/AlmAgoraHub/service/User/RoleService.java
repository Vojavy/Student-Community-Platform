package com.vojavy.AlmAgoraHub.service.User;

import com.vojavy.AlmAgoraHub.model.user.Role;
import com.vojavy.AlmAgoraHub.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
    }
}

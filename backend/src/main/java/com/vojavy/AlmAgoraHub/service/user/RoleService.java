package com.vojavy.AlmAgoraHub.service.user;

import com.vojavy.AlmAgoraHub.model.user.Role;
import com.vojavy.AlmAgoraHub.model.user.RoleType;
import com.vojavy.AlmAgoraHub.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getByName(String roleName) {
        return roleRepository.findByName(RoleType.fromString(roleName))
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
    }

    public Optional<Role> getById(Long roleId){
        return roleRepository.findById(roleId);
    }


    public List<Role> getAll() {
        return roleRepository.findAll();
    }
}

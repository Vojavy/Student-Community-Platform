package com.vojavy.AlmAgoraHub.config;

import com.vojavy.AlmAgoraHub.repository.RoleRepository;
import com.vojavy.AlmAgoraHub.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        List<String> defaultRoles = List.of("ROLE_USER", "ROLE_LIMITED", "ROLE_ADMIN");

        for (String roleName : defaultRoles) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(new Role(roleName)));
        }
    }
}

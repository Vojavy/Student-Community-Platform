// src/main/java/com/vojavy/AlmAgoraHub/config/DataInitializer.java
package com.vojavy.AlmAgoraHub.config;

import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.model.user.Role;
import com.vojavy.AlmAgoraHub.model.user.RoleType;
import com.vojavy.AlmAgoraHub.repository.RoleRepository;
import com.vojavy.AlmAgoraHub.repository.UniversityDomainRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UniversityDomainRepository domainRepository;

    public DataInitializer(
            RoleRepository roleRepository,
            UniversityDomainRepository domainRepository
    ) {
        this.roleRepository = roleRepository;
        this.domainRepository = domainRepository;
    }

    @Override
    public void run(String... args) {
        initializeRoles();
        initializeDomains();
    }

    private void initializeRoles() {
        for (RoleType rt : RoleType.values()) {
            String name = rt.name();
            roleRepository.findByName(RoleType.fromString(name))
                    .orElseGet(() -> roleRepository.save(new Role(name)));
        }
    }

    private void initializeDomains() {
        var defaults = List.of(
                new UniversityDomain(
                        "Univerzita Pardubice",
                        "upce",
                        "admin@upce.cz",
                        "https://portal.upce.cz/"
                ),
                new UniversityDomain(
                        "Západočeská univerzita v Plzni",
                        "zcu",
                        "admin@zcu.cz",
                        "https://portal.zcu.cz/"
                )
        );

        for (UniversityDomain ud : defaults) {
            domainRepository.findByDomain(ud.getDomain())
                    .orElseGet(() -> domainRepository.save(ud));
        }
    }
}

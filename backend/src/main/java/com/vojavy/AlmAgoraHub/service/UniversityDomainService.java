package com.vojavy.AlmAgoraHub.service;

import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.repository.UniversityDomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UniversityDomainService {

    private final UniversityDomainRepository domainRepository;

    @Autowired
    public UniversityDomainService(UniversityDomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public Optional<UniversityDomain> getDomainByCode(String domainCode) {
        return Optional.ofNullable(domainRepository
                .findByDomain(domainCode)
                .orElseThrow(() -> new IllegalArgumentException("University domain not found: " + domainCode)));
    }
}

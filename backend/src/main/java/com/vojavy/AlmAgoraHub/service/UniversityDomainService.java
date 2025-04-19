package com.vojavy.AlmAgoraHub.service;

import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.repository.UniversityDomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UniversityDomainService {

    private final UniversityDomainRepository domainRepository;

    @Autowired
    public UniversityDomainService(UniversityDomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public UniversityDomain getDomainByCode(String domainCode) {
        return domainRepository
                .findByDomain(domainCode)
                .orElseThrow(() -> new IllegalArgumentException("University domain not found: " + domainCode));
    }
}

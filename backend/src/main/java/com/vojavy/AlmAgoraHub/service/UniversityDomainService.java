package com.vojavy.AlmAgoraHub.service;

import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.repository.UniversityDomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UniversityDomainService {

    private final UniversityDomainRepository domainRepository;

    @Autowired
    public UniversityDomainService(UniversityDomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    /** Возвращает все домены */
    public List<UniversityDomain> getAllDomains() {
        return domainRepository.findAll();
    }

    /** Ищет домен по его ID */
    public Optional<UniversityDomain> getDomainById(Long id) {
        return domainRepository.findById(id);
    }

    /** Ищет домен по его «code» (поле domain) */
    public Optional<UniversityDomain> getDomainByCode(String domainCode) {
        return domainRepository.findByDomain(domainCode);
    }
}

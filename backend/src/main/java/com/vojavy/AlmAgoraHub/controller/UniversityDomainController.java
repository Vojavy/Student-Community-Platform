package com.vojavy.AlmAgoraHub.controller;

import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.service.UniversityDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/university-domains")
public class UniversityDomainController {

    private final UniversityDomainService domainService;

    @Autowired
    public UniversityDomainController(UniversityDomainService domainService) {
        this.domainService = domainService;
    }

    @GetMapping
    public List<UniversityDomain> getAll() {
        return domainService.getAllDomains();
    }

    @GetMapping("/{id}")
    public UniversityDomain getById(@PathVariable Long id) {
        return domainService.getDomainById(id).orElse(null);
    }

    @GetMapping("/code/{domain}")
    public UniversityDomain getByCode(@PathVariable("domain") String code) {
        return domainService.getDomainByCode(code).orElse(null);
    }
}

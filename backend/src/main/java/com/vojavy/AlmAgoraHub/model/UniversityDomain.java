package com.vojavy.AlmAgoraHub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "university_domains")
public class UniversityDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "domain_name", nullable = false)
    private String domainName;

    @Column(name = "domain", nullable = false)
    private String domain;

    @Column(name = "website_url", nullable = false)
    private String websiteUrl;

    @Column(name = "admin_email", nullable = false)
    private String adminEmail;

    public UniversityDomain() {}

    public UniversityDomain(String domainName, String domain, String websiteUrl, String adminEmail) {
        this.domainName = domainName;
        this.domain = domain;
        this.websiteUrl = websiteUrl;
        this.adminEmail = adminEmail;
    }
    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
}

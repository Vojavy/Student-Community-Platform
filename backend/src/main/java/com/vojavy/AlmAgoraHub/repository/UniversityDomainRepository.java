package com.vojavy.AlmAgoraHub.repository;


import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UniversityDomainRepository extends JpaRepository<UniversityDomain, Long> {
    Optional<UniversityDomain> findByDomain(String domain);
}

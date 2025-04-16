package com.vojavy.AlmAgoraHub.repository;

import com.vojavy.AlmAgoraHub.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Role, Long> {
}

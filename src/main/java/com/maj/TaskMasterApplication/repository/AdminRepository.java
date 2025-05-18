package com.maj.TaskMasterApplication.repository;

import com.maj.TaskMasterApplication.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Admin> findByUsername(String username);
    boolean existsByUsername(String username);
}

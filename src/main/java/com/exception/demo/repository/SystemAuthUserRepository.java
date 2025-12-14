package com.exception.demo.repository;

import com.exception.demo.model.SystemAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemAuthUserRepository extends JpaRepository<SystemAuthUser, Integer> {
    SystemAuthUser findByUsername(String username);
}

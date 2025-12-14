package com.exception.demo.repository;

import com.exception.demo.model.TaskChecker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskCheckerRepository extends JpaRepository<TaskChecker, Long> {

    boolean existsByLevel_IdAndUser_Username(Long levelId, String username);
}

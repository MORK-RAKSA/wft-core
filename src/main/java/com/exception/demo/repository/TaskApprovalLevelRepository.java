package com.exception.demo.repository;

import com.exception.demo.model.TaskApprovalLevel;
import com.exception.demo.workflow.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskApprovalLevelRepository extends JpaRepository<TaskApprovalLevel, Long> {

    TaskApprovalLevel findByTask_IdAndLevelNumber(Long taskId, Integer levelNumber);

    boolean existsByTask_IdAndLevelNumberAndStatus(Long taskId, Integer levelNumber, Status status);
}

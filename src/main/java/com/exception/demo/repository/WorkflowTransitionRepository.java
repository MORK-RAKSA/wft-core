package com.exception.demo.repository;

import com.exception.demo.model.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, Integer> {

    WorkflowTransition findByFromStep_IdAndIsApproved(Integer stepId, boolean isApproved);
}

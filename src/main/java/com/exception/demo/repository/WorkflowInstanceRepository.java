package com.exception.demo.repository;

import com.exception.demo.model.WorkflowInstance;
import com.exception.demo.workflow.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {

    WorkflowInstance findByTask_IdAndStatus(long taskId, Status status);
}

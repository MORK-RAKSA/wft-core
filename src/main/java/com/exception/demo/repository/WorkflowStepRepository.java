package com.exception.demo.repository;

import com.exception.demo.model.WorkflowStep;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Integer> {
    List<WorkflowStep> findByWorkflow_Id(Integer id);

    WorkflowStep findFirstByWorkflow_IdOrderByStepNumberAsc(Integer id);

    List<WorkflowStep> findByWorkflow_IdOrderByStepNumberAsc(Integer id);
}

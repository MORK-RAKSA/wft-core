package com.exception.demo.repository;

import com.exception.demo.model.WorkflowApprovalLevel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowApprovalLevelRepository extends
    JpaRepository<WorkflowApprovalLevel, Integer> {

//    List<WorkflowApprovalLevel> findByStepIdOrderByLevelNumberAsc(Integer id);
//
    WorkflowApprovalLevel findByStep_Id(Integer stepId);

    List<WorkflowApprovalLevel> findAllByStep_Id(Integer id);
}

package com.exception.demo.repository;

import com.exception.demo.model.WorkflowChecker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowCheckerRepository extends JpaRepository<WorkflowChecker, String> {

    List<WorkflowChecker> findById_Level_Id(Integer id);
}

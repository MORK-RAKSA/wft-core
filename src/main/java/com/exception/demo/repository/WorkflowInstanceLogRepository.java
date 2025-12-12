package com.exception.demo.repository;

import com.exception.demo.model.WorkflowInstanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowInstanceLogRepository extends JpaRepository<WorkflowInstanceLog, Long> {

    @Query("""
    SELECT COUNT(l) > 0
    FROM WorkflowInstanceLog l
    LEFT JOIN WorkflowInstanceAssignment a
         ON a.instance.id = l.instance.id
    LEFT JOIN TaskChecker tc
         ON tc.id = a.checker.id
    WHERE l.instance.id = :instanceId
      AND l.step.id = :stepId
      AND a.approvalLevel.id = :levelId
      AND tc.user.username = :username
      AND l.decidedBy = :username
      AND l.isApproved = TRUE
""")
    boolean hasUserAlreadyApproved(
        @Param("instanceId") Long instanceId,
        @Param("stepId") Integer stepId,
        @Param("levelId") Long levelId,
        @Param("username") String username
    );


    @Query("""
    SELECT COUNT(DISTINCT l.decidedBy)
    FROM WorkflowInstanceLog l
    JOIN WorkflowInstanceAssignment a ON a.instance.id = l.instance.id
    JOIN TaskChecker tc ON tc.id = a.checker.id
    WHERE l.instance.id = :instanceId
      AND l.step.id = :stepId
      AND a.approvalLevel.id = :levelId
      AND l.decidedBy = tc.user.username
      AND l.isApproved = TRUE
""")
    int countValidApprovals(Long instanceId, Integer stepId, Long levelId);

}

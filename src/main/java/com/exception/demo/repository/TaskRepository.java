package com.exception.demo.repository;

import com.exception.demo.model.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select t.id from Task t where t.id in :ids ")
    List<Task> findTaskByIds(List<Long> ids);

    @Modifying
    @Query("UPDATE Task t SET t.status =:status WHERE t.id = :taskId")
    void updateApproved(@Param("taskId") Long taskId, @Param("status") String status);
}

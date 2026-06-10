package com.dube.workflow.workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WorkflowHistoryRepository extends JpaRepository<WorkflowHistory, UUID> {
    List<WorkflowHistory> findByJobIdOrderByChangedAtDesc(UUID jobId);
}
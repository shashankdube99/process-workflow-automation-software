package com.dube.workflow.workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CustomerApprovalRepository extends JpaRepository<CustomerApproval, UUID> {
}
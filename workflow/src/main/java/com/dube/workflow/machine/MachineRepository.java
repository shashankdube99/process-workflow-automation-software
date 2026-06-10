package com.dube.workflow.machine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MachineRepository extends JpaRepository<Machine, UUID> {
    List<Machine> findByStatus(String status);
}
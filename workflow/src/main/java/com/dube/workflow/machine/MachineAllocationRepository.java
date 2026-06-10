package com.dube.workflow.machine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface MachineAllocationRepository extends JpaRepository<MachineAllocation, UUID> {
    Optional<MachineAllocation> findByMachineIdAndReleasedAtIsNull(UUID machineId);
}
package com.dube.workflow.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    
    // --- YOUR EXISTING QUERIES ---
    
    // Finds the latest job for a given year to generate the next sequence (JOB-YYYY-XXXX)
    @Query(value = "SELECT j.job_number FROM jobs j WHERE j.job_number LIKE CONCAT('JOB-', :year, '-%') ORDER BY j.job_number DESC LIMIT 1", nativeQuery = true)
    Optional<String> findLatestJobNumberForYear(@Param("year") int year);

    List<Job> findByIsDeletedFalse();
    
    @Query("SELECT j FROM Job j WHERE j.isDeleted = false AND (" +
           "LOWER(j.jobNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(j.customer.companyName) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Job> searchJobs(@Param("search") String search);


    // --- NEW: QUERIES FOR ANALYTICS DASHBOARD & LOOKUPS ---
    
    List<Job> findByJobNumberContainingIgnoreCase(String jobNumber);
    
    List<Job> findByCustomerId(UUID customerId);

    // Count jobs that are NOT completed or cancelled
    long countByStatusNotIn(List<String> statuses);
    
    // Count jobs currently waiting for a specific status (e.g., AWAITING_CUSTOMER_APPROVAL)
    long countByStatus(String status);
}
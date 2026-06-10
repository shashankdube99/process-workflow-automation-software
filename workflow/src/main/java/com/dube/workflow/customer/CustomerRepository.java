package com.dube.workflow.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);

    // Advanced search for the frontend grid (Day 4 Requirement: Search by Name, Mobile, GST)
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "c.mobile LIKE CONCAT('%', :query, '%') OR " +
           "LOWER(c.gstNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Customer> searchCustomers(@Param("query") String query);
}
package com.dube.workflow.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    // NEW: Calculate total successful revenue
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PAID'")
    BigDecimal calculateTotalRevenue();
}
package com.dube.workflow.payment;

import com.dube.workflow.payment.dto.OrderRequestDTO;
import com.dube.workflow.payment.dto.PaymentVerificationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 🚀 STEP 1: Create Order
    @PostMapping("/create-order")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'RECEPTIONIST')")
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody OrderRequestDTO request) {
        String orderId = paymentService.createOrder(request);
        return ResponseEntity.ok(Map.of("razorpayOrderId", orderId));
    }

    // 🚀 STEP 2: Verify Payment
    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'RECEPTIONIST')")
    public ResponseEntity<Map<String, String>> verifyPayment(@RequestBody PaymentVerificationDTO request) {
        paymentService.verifyPayment(request);
        return ResponseEntity.ok(Map.of("message", "Payment verified successfully"));
    }
}
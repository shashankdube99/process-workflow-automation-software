package com.dube.workflow.payment;

import com.dube.workflow.exception.BadRequestException;
import com.dube.workflow.exception.ResourceNotFoundException;
import com.dube.workflow.job.Job;
import com.dube.workflow.job.JobRepository;
import com.dube.workflow.payment.dto.OrderRequestDTO;
import com.dube.workflow.payment.dto.PaymentVerificationDTO;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final JobRepository jobRepository;

    @Value("${app.razorpay.key-id}")
    private String keyId;

    @Value("${app.razorpay.key-secret}")
    private String keySecret;

    public PaymentService(PaymentRepository paymentRepository, JobRepository jobRepository) {
        this.paymentRepository = paymentRepository;
        this.jobRepository = jobRepository;
    }

    @Transactional
    public String createOrder(OrderRequestDTO request) {
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        try {
            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

            // Razorpay expects amount in minimum currency units (Paise for INR)
            int amountInPaise = request.getAmount().multiply(new BigDecimal("100")).intValue();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = razorpay.orders.create(orderRequest);
            String razorpayOrderId = order.get("id");

            // Save pending payment record
            Payment payment = new Payment();
            payment.setJob(job);
            payment.setAmount(request.getAmount());
            payment.setRazorpayOrderId(razorpayOrderId);
            payment.setStatus("PENDING");
            paymentRepository.save(payment);

            return razorpayOrderId;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order", e);
        }
    }

    @Transactional
    public void verifyPayment(PaymentVerificationDTO verification) {
        try {
            // Recreate the signature to verify its authenticity
            String signaturePayload = verification.getRazorpayOrderId() + "|" + verification.getRazorpayPaymentId();
            boolean isValidSignature = Utils.verifySignature(signaturePayload, verification.getRazorpaySignature(), keySecret);

            if (!isValidSignature) {
                throw new BadRequestException("Invalid payment signature");
            }

            Payment payment = paymentRepository.findByRazorpayOrderId(verification.getRazorpayOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

            // Update Payment Record
            payment.setRazorpayPaymentId(verification.getRazorpayPaymentId());
            payment.setRazorpaySignature(verification.getRazorpaySignature());
            payment.setStatus("PAID");
            paymentRepository.save(payment);

            // Update Job Payment Status
            Job job = payment.getJob();
            job.setPaymentStatus("PAID");
            jobRepository.save(job);

        } catch (Exception e) {
            throw new RuntimeException("Payment verification failed", e);
        }
    }
}
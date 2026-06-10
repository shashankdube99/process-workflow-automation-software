package com.dube.workflow.report;

import com.dube.workflow.customer.CustomerRepository;
import com.dube.workflow.job.JobRepository;
import com.dube.workflow.payment.PaymentRepository;
import com.dube.workflow.report.dto.DashboardStatsDTO;
import org.springframework.stereotype.Service;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.awt.Color;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class ReportService {

    private final JobRepository jobRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;

    public ReportService(JobRepository jobRepository, PaymentRepository paymentRepository, CustomerRepository customerRepository) {
        this.jobRepository = jobRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
    }

    public DashboardStatsDTO getDashboardStatistics() {
        // 1. Calculate Revenue (Handle null if no payments exist yet)
        BigDecimal totalRevenue = paymentRepository.calculateTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        // 2. Calculate Active Jobs (Exclude Completed and Cancelled)
        long activeJobs = jobRepository.countByStatusNotIn(Arrays.asList("COMPLETED", "CANCELLED"));

        // 3. Calculate Pending Approvals
        long pendingApprovals = jobRepository.countByStatus("AWAITING_CUSTOMER_APPROVAL");

        // 4. Calculate Total Customers
        long totalCustomers = customerRepository.count();

        return new DashboardStatsDTO(totalRevenue, activeJobs, pendingApprovals, totalCustomers);
    }
    
    public byte[] generatePdfReport() {
        // Get the latest stats using the method we wrote earlier
        DashboardStatsDTO stats = getDashboardStatistics();

        // Create a byte array to hold the PDF data in memory
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add Title
            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
            Paragraph title = new Paragraph("Business Performance Report", titleFont);
            title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add Timestamp
            com.lowagie.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);
            Paragraph timestamp = new Paragraph("Generated on: " + java.time.LocalDateTime.now().toString(), normalFont);
            timestamp.setSpacingAfter(30);
            document.add(timestamp);

            // Add Metrics
            com.lowagie.text.Font metricFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            
            document.add(new Paragraph("Total Revenue: Rs. " + stats.getTotalRevenue(), metricFont));
            document.add(new Paragraph("Active Jobs: " + stats.getActiveJobs(), metricFont));
            document.add(new Paragraph("Pending Approvals: " + stats.getPendingApprovals(), metricFont));
            document.add(new Paragraph("Total Customers: " + stats.getTotalCustomers(), metricFont));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }

        return out.toByteArray();
    }
}
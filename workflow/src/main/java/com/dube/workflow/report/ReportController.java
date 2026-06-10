package com.dube.workflow.report;

import com.dube.workflow.report.dto.DashboardStatsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard-stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(reportService.getDashboardStatistics());
    }
    
    @GetMapping("/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<byte[]> downloadReport() {
        byte[] pdfBytes = reportService.generatePdfReport();

        HttpHeaders headers = new HttpHeaders();
        // This header tells the browser to trigger a "Save As" download prompt
        headers.setContentDispositionFormData("attachment", "Business_Report.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
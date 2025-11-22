package com.corebank.coreb.controllers;

import com.corebank.coreb.dto.RepaymentReportDTO;
import com.corebank.coreb.entity.Repayment;
import com.corebank.coreb.service.RepaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/repayments")
public class RepaymentController {

    @Autowired
    private RepaymentService repaymentService;

    // --------------------
    // Create a new repayment manually
    // --------------------
    @PostMapping
    public ResponseEntity<Repayment> createRepayment(@RequestBody Repayment repayment) {
        Repayment saved = repaymentService.saveRepayment(repayment);
        return ResponseEntity.ok(saved);
    }

    // --------------------
    // Update a repayment manually
    // --------------------
    @PutMapping("/{repaymentId}")
    public ResponseEntity<Repayment> updateRepayment(@PathVariable Long repaymentId,
                                                     @RequestBody Repayment repayment) {
        repayment.setRepaymentId(repaymentId);
        Repayment updated = repaymentService.updateRepayment(repayment);
        return ResponseEntity.ok(updated);
    }

    // --------------------
    // Process Payment (Partial / Full)
    // --------------------
    @PutMapping("/{repaymentId}/pay")
    public ResponseEntity<Repayment> processPayment(@PathVariable Long repaymentId,
                                                    @RequestParam BigDecimal amountPaid) {
        Repayment updated = repaymentService.processPayment(repaymentId, amountPaid);
        return ResponseEntity.ok(updated);
    }

    // --------------------
    // Get repayment by ID
    // --------------------
    @GetMapping("/{repaymentId}")
    public ResponseEntity<Repayment> getRepaymentById(@PathVariable Long repaymentId) {
        Optional<Repayment> repayment = repaymentService.getRepaymentById(repaymentId);
        return repayment.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    // --------------------
    // Get all repayments
    // --------------------
    @GetMapping
    public ResponseEntity<List<Repayment>> getAllRepayments() {
        List<Repayment> repayments = repaymentService.getAllRepayments();
        return ResponseEntity.ok(repayments);
    }

    // --------------------
    // Get repayments by loan ID
    // --------------------
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<Repayment>> getByLoan(@PathVariable Long loanId) {
        List<Repayment> repayments = repaymentService.getRepaymentsByLoan(loanId);
        return ResponseEntity.ok(repayments);
    }


    // --------------------
    // Delete repayment (if not paid)
    // --------------------
    @DeleteMapping("/{repaymentId}")
    public ResponseEntity<String> deleteRepayment(@PathVariable Long repaymentId) {
        boolean success = repaymentService.deleteRepayment(repaymentId);
        if (success) {
            return ResponseEntity.ok("Repayment deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Cannot delete a paid repayment.");
        }
    }
    
    //-------------------
    //Get Repayment Report
    //-------------------
    @GetMapping("/report")
    public ResponseEntity<List<RepaymentReportDTO>> getRepaymentReport(
            @RequestParam(required = false) Long loanId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<RepaymentReportDTO> report = repaymentService.generateReport(loanId, startDate, endDate);
        return ResponseEntity.ok(report);
    }
    
  //-------------------
 // Get Repayment Report PDF (Modern Styled)
 //-------------------
 @GetMapping("/report/pdf")
 public ResponseEntity<byte[]> downloadRepaymentReportPdf(
         @RequestParam(required = false) String branchCode,
         @RequestParam(required = false) Long loanId,
         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
 ) {

     // Get username from security/jwt if available
     String generatedBy = "System"; // or you can extract from JWT token later

     byte[] pdfBytes = repaymentService.exportPdf(loanId, startDate, endDate, generatedBy);

     HttpHeaders headers = new HttpHeaders();
     headers.add("Content-Disposition", "attachment; filename=repayment_report.pdf");

     return ResponseEntity.ok()
             .headers(headers)
             .contentType(MediaType.APPLICATION_PDF)
             .body(pdfBytes);
 }
 
}

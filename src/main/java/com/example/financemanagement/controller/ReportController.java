package com.example.financemanagement.controller;

import com.example.financemanagement.dto.MonthlyReport;
import com.example.financemanagement.dto.YearlyReport;
import com.example.financemanagement.exception.BadRequestException;
import com.example.financemanagement.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for generating financial reports.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Gets the monthly financial report for a specific year and month.
     * @param year The year of the report.
     * @param month The month of the report.
     * @return A response entity containing the report data.
     */
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReport> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month) {
        // Validate month parameter
        if (month < 1 || month > 12) {
            throw new BadRequestException("Month must be between 1 and 12");
        }
        MonthlyReport report = reportService.generateMonthlyReport(year, month);
        return ResponseEntity.ok(report);
    }

    /**
     * Gets the yearly financial report for a specific year.
     * @param year The year of the report.
     * @return A response entity containing the report data.
     */
    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReport> getYearlyReport(@PathVariable int year) {
        YearlyReport report = reportService.generateYearlyReport(year);
        return ResponseEntity.ok(report);
    }
} 
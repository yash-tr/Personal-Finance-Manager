package com.example.financemanagement.controller;

import com.example.financemanagement.dto.MonthlyReport;
import com.example.financemanagement.dto.YearlyReport;
import com.example.financemanagement.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@Import({com.example.financemanagement.config.SecurityConfig.class,
         com.example.financemanagement.exception.GlobalExceptionHandler.class})
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    private MonthlyReport monthlyReport;
    private YearlyReport yearlyReport;

    @BeforeEach
    void setUp() {
        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        incomeByCategory.put("Salary", BigDecimal.valueOf(3000.00));

        Map<String, BigDecimal> expensesByCategory = new HashMap<>();
        expensesByCategory.put("Food", BigDecimal.valueOf(400.00));
        expensesByCategory.put("Rent", BigDecimal.valueOf(1200.00));

        monthlyReport = new MonthlyReport(1, 2024, incomeByCategory, expensesByCategory, BigDecimal.valueOf(1400.00));

        Map<String, BigDecimal> yearlyIncome = new HashMap<>();
        yearlyIncome.put("Salary", BigDecimal.valueOf(36000.00));
        yearlyIncome.put("Freelance", BigDecimal.valueOf(6000.00));

        Map<String, BigDecimal> yearlyExpenses = new HashMap<>();
        yearlyExpenses.put("Food", BigDecimal.valueOf(4800.00));
        yearlyExpenses.put("Rent", BigDecimal.valueOf(14400.00));
        yearlyExpenses.put("Transportation", BigDecimal.valueOf(2400.00));

        yearlyReport = new YearlyReport(2024, yearlyIncome, yearlyExpenses, BigDecimal.valueOf(20400.00));
    }

    @Test
    @WithMockUser
    void getMonthlyReport_Success() throws Exception {
        when(reportService.generateMonthlyReport(2024, 1)).thenReturn(monthlyReport);

        mockMvc.perform(get("/api/reports/monthly/2024/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value(1))
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.netSavings").value(1400.00));
    }

    @Test
    @WithMockUser
    void getYearlyReport_Success() throws Exception {
        when(reportService.generateYearlyReport(2024)).thenReturn(yearlyReport);

        mockMvc.perform(get("/api/reports/yearly/2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.netSavings").value(20400.00));
    }

    @Test
    void getMonthlyReport_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/reports/monthly/2024/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getYearlyReport_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/reports/yearly/2024"))
                .andExpect(status().isUnauthorized());
    }
} 
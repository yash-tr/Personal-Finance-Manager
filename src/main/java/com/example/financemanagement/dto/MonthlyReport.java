package com.example.financemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReport {
    private int month;
    private int year;
    private Map<String, BigDecimal> totalIncome;
    private Map<String, BigDecimal> totalExpenses;
    private BigDecimal netSavings;
} 
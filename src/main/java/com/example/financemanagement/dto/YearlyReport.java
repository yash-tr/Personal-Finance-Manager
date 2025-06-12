package com.example.financemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearlyReport {
    private int year;
    private Map<String, BigDecimal> totalIncome;
    private Map<String, BigDecimal> totalExpenses;
    private BigDecimal netSavings;
} 
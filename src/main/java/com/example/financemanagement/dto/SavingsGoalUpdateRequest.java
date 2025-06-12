package com.example.financemanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SavingsGoalUpdateRequest {
    @DecimalMin(value = "0.0", inclusive = false, message = "Target amount must be positive")
    private BigDecimal targetAmount;
    
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;
} 
package com.example.financemanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SavingsGoalRequest {
    @NotBlank(message = "Goal name cannot be blank")
    private String goalName;

    @NotNull(message = "Target amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Target amount must be positive")
    private BigDecimal targetAmount;

    @NotNull(message = "Target date cannot be null")
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;
    
    private LocalDate startDate;
    
    // Custom validation method
    public boolean isValidDateRange() {
        if (startDate != null && targetDate != null) {
            return !startDate.isAfter(targetDate);
        }
        return true;
    }
} 
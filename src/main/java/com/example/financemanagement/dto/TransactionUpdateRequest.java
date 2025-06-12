package com.example.financemanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionUpdateRequest {
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;
    
    private String description;

    private String category;
} 
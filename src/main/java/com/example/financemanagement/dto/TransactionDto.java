package com.example.financemanagement.dto;

import com.example.financemanagement.entity.CategoryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDto {
    private Long id;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotNull
    private LocalDate date;

    private CategoryType type;

    private Long userId;

    @NotBlank
    private String categoryName;

    private String description;
} 
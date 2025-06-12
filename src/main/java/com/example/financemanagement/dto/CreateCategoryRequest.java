package com.example.financemanagement.dto;

import com.example.financemanagement.entity.CategoryType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    private CategoryType type;
} 
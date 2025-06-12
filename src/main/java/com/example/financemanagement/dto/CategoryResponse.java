package com.example.financemanagement.dto;

import com.example.financemanagement.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private String name;
    private CategoryType type;
    private boolean isCustom;
} 
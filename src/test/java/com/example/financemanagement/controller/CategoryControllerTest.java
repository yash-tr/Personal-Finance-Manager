package com.example.financemanagement.controller;

import com.example.financemanagement.dto.CategoryResponse;
import com.example.financemanagement.dto.CreateCategoryRequest;
import com.example.financemanagement.entity.CategoryType;
import com.example.financemanagement.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import({com.example.financemanagement.config.SecurityConfig.class, 
         com.example.financemanagement.exception.GlobalExceptionHandler.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<CategoryResponse> categories;
    private CreateCategoryRequest validRequest;
    private CategoryResponse validResponse;

    @BeforeEach
    void setUp() {
        categories = Arrays.asList(
                new CategoryResponse("Salary", CategoryType.INCOME, false),
                new CategoryResponse("Food", CategoryType.EXPENSE, false),
                new CategoryResponse("Custom Category", CategoryType.EXPENSE, true)
        );

        validRequest = new CreateCategoryRequest("SideBusinessIncome", CategoryType.INCOME);
        validResponse = new CategoryResponse("SideBusinessIncome", CategoryType.INCOME, true);
    }

    @Test
    @WithMockUser
    void getAllCategories_Success() throws Exception {
        when(categoryService.findAllByCurrentUser()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories").value(org.hamcrest.Matchers.hasSize(3)))
                .andExpect(jsonPath("$.categories[0].name").value("Salary"))
                .andExpect(jsonPath("$.categories[0].type").value("INCOME"))
                .andExpect(jsonPath("$.categories[0].custom").value(false));
    }

    @Test
    @WithMockUser
    void createCustomCategory_Success() throws Exception {
        when(categoryService.createCustomCategory(any(CreateCategoryRequest.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SideBusinessIncome"))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.custom").value(true));
    }

    @Test
    @WithMockUser
    void createCustomCategory_InvalidName_BadRequest() throws Exception {
        CreateCategoryRequest invalidRequest = new CreateCategoryRequest("", CategoryType.INCOME);

        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void deleteCategory_Success() throws Exception {
        doNothing().when(categoryService).deleteCategoryByName("CustomCategory");

        mockMvc.perform(delete("/api/categories/CustomCategory")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }

    @Test
    void getAllCategories_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCustomCategory_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteCategory_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/categories/CustomCategory")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
} 
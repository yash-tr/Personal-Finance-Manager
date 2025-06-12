package com.example.financemanagement.controller;

import com.example.financemanagement.dto.TransactionRequest;
import com.example.financemanagement.dto.TransactionResponse;
import com.example.financemanagement.entity.CategoryType;
import com.example.financemanagement.service.TransactionService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import({com.example.financemanagement.config.SecurityConfig.class,
         com.example.financemanagement.exception.GlobalExceptionHandler.class})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionRequest validRequest;
    private TransactionResponse validResponse;

    @BeforeEach
    void setUp() {
        validRequest = new TransactionRequest(
                BigDecimal.valueOf(100.00),
                LocalDate.of(2024, 1, 15),
                "Salary",
                "Monthly salary"
        );

        validResponse = new TransactionResponse(
                1L,
                BigDecimal.valueOf(100.00),
                LocalDate.of(2024, 1, 15),
                "Salary",
                "Monthly salary",
                CategoryType.INCOME
        );
    }

    @Test
    @WithMockUser
    void createTransaction_Success() throws Exception {
        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.category").value("Salary"))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    @WithMockUser
    void createTransaction_InvalidAmount_BadRequest() throws Exception {
        TransactionRequest invalidRequest = new TransactionRequest(
                BigDecimal.valueOf(-100.00),
                LocalDate.of(2024, 1, 15),
                "Salary",
                "Monthly salary"
        );

        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createTransaction_FutureDate_BadRequest() throws Exception {
        TransactionRequest invalidRequest = new TransactionRequest(
                BigDecimal.valueOf(100.00),
                LocalDate.now().plusDays(1),
                "Salary",
                "Monthly salary"
        );

        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getTransactions_Success() throws Exception {
        List<TransactionResponse> transactions = Arrays.asList(validResponse);
        when(transactionService.getTransactions(any(), any(), any())).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser
    void getTransactions_WithFilters_Success() throws Exception {
        List<TransactionResponse> transactions = Arrays.asList(validResponse);
        when(transactionService.getTransactions(any(), any(), any())).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-31")
                .param("category", "Salary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void getTransactionById_Success() throws Exception {
        when(transactionService.getTransactionById(1L)).thenReturn(validResponse);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("Salary"));
    }

    @Test
    @WithMockUser
    void updateTransaction_Success() throws Exception {
        TransactionResponse updatedResponse = new TransactionResponse(
                1L,
                BigDecimal.valueOf(150.00),
                LocalDate.of(2024, 1, 15),
                "Salary",
                "Updated salary",
                CategoryType.INCOME
        );

        when(transactionService.updateTransaction(eq(1L), any(TransactionRequest.class))).thenReturn(updatedResponse);

        TransactionRequest updateRequest = new TransactionRequest(
                BigDecimal.valueOf(150.00),
                LocalDate.of(2024, 1, 15),
                "Salary",
                "Updated salary"
        );

        mockMvc.perform(put("/api/transactions/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.description").value("Updated salary"));
    }

    @Test
    @WithMockUser
    void deleteTransaction_Success() throws Exception {
        doNothing().when(transactionService).deleteTransaction(1L);

        mockMvc.perform(delete("/api/transactions/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));
    }

    @Test
    void createTransaction_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTransactions_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isUnauthorized());
    }
} 
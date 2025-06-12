package com.example.financemanagement.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.financemanagement.controller.TransactionController;
import com.example.financemanagement.service.TransactionService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    @WithMockUser
    void handleResourceNotFoundException() throws Exception {
        when(transactionService.getTransactionById(999L))
                .thenThrow(new ResourceNotFoundException("Transaction not found with id: 999"));

        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void handleForbiddenException() throws Exception {
        when(transactionService.getTransactionById(1L))
                .thenThrow(new ForbiddenException("You are not authorized to view this transaction"));

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void handleBadRequestException() throws Exception {
        doThrow(new BadRequestException("Cannot delete transaction that is in use"))
                .when(transactionService).deleteTransaction(1L);

        mockMvc.perform(delete("/api/transactions/1")
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void handleResourceConflictException() throws Exception {
        when(transactionService.createTransaction(any()))
                .thenThrow(new ResourceConflictException("Category with this name already exists"));

        String requestBody = """
                {
                    "amount": 100.00,
                    "date": "2024-01-15",
                    "category": "Salary",
                    "description": "Test"
                }
                """;

        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
} 
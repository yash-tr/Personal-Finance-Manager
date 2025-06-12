package com.example.financemanagement.controller;

import com.example.financemanagement.dto.SavingsGoalRequest;
import com.example.financemanagement.dto.SavingsGoalResponse;
import com.example.financemanagement.dto.SavingsGoalUpdateRequest;
import com.example.financemanagement.service.SavingsGoalService;
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

@WebMvcTest(SavingsGoalController.class)
@Import({com.example.financemanagement.config.SecurityConfig.class,
         com.example.financemanagement.exception.GlobalExceptionHandler.class})
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SavingsGoalService savingsGoalService;

    @Autowired
    private ObjectMapper objectMapper;

    private SavingsGoalRequest validRequest;
    private SavingsGoalResponse validResponse;

    @BeforeEach
    void setUp() {
        validRequest = new SavingsGoalRequest();
        validRequest.setGoalName("Emergency Fund");
        validRequest.setTargetAmount(BigDecimal.valueOf(10000.00));
        validRequest.setTargetDate(LocalDate.now().plusMonths(6));

        validResponse = new SavingsGoalResponse(
                1L,
                "Emergency Fund",
                BigDecimal.valueOf(10000.00),
                LocalDate.now().plusMonths(6),
                LocalDate.now(),
                BigDecimal.valueOf(2500.00),
                25.0,
                BigDecimal.valueOf(7500.00)
        );
    }

    @Test
    @WithMockUser
    void createGoal_Success() throws Exception {
        when(savingsGoalService.createSavingsGoal(any(SavingsGoalRequest.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/goals")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.goalName").value("Emergency Fund"))
                .andExpect(jsonPath("$.targetAmount").value(10000.00));
    }

    @Test
    @WithMockUser
    void getAllGoals_Success() throws Exception {
        List<SavingsGoalResponse> goals = Arrays.asList(validResponse);
        when(savingsGoalService.getAllSavingsGoals()).thenReturn(goals);

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals").isArray())
                .andExpect(jsonPath("$.goals[0].id").value(1));
    }

    @Test
    @WithMockUser
    void getGoalById_Success() throws Exception {
        when(savingsGoalService.getSavingsGoalById(1L)).thenReturn(validResponse);

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.goalName").value("Emergency Fund"));
    }

    @Test
    @WithMockUser
    void updateGoal_Success() throws Exception {
        SavingsGoalUpdateRequest updateRequest = new SavingsGoalUpdateRequest();
        updateRequest.setTargetAmount(BigDecimal.valueOf(15000.00));
        updateRequest.setTargetDate(LocalDate.now().plusMonths(8));

        SavingsGoalResponse updatedResponse = new SavingsGoalResponse(
                1L,
                "Emergency Fund",
                BigDecimal.valueOf(15000.00),
                LocalDate.now().plusMonths(8),
                LocalDate.now(),
                BigDecimal.valueOf(2500.00),
                16.67,
                BigDecimal.valueOf(12500.00)
        );

        when(savingsGoalService.updateSavingsGoal(eq(1L), any(SavingsGoalUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/goals/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetAmount").value(15000.00));
    }

    @Test
    @WithMockUser
    void deleteGoal_Success() throws Exception {
        doNothing().when(savingsGoalService).deleteSavingsGoal(1L);

        mockMvc.perform(delete("/api/goals/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal deleted successfully"));
    }

    @Test
    @WithMockUser
    void createGoal_InvalidTargetDate_BadRequest() throws Exception {
        SavingsGoalRequest invalidRequest = new SavingsGoalRequest();
        invalidRequest.setGoalName("Emergency Fund");
        invalidRequest.setTargetAmount(BigDecimal.valueOf(10000.00));
        invalidRequest.setTargetDate(LocalDate.now().minusMonths(1)); // Past date

        mockMvc.perform(post("/api/goals")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createGoal_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/goals")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllGoals_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isUnauthorized());
    }
} 
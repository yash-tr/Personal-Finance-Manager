package com.example.financemanagement.controller;

import com.example.financemanagement.dto.SavingsGoalRequest;
import com.example.financemanagement.dto.SavingsGoalResponse;
import com.example.financemanagement.dto.SavingsGoalUpdateRequest;
import com.example.financemanagement.service.SavingsGoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for managing savings goals.
 */
@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {
    @Autowired
    private SavingsGoalService savingsGoalService;

    /**
     * Creates a new savings goal.
     * @param request The savings goal data from the request body.
     * @return The created savings goal.
     */
    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createGoal(@Valid @RequestBody SavingsGoalRequest request) {
        SavingsGoalResponse createdGoal = savingsGoalService.createSavingsGoal(request);
        return new ResponseEntity<>(createdGoal, HttpStatus.CREATED);
    }

    /**
     * Retrieves all savings goals for the current user.
     * @return A list of savings goals.
     */
    @GetMapping
    public ResponseEntity<Map<String, List<SavingsGoalResponse>>> getAllGoals() {
        List<SavingsGoalResponse> goals = savingsGoalService.getAllSavingsGoals();
        return ResponseEntity.ok(Map.of("goals", goals));
    }

    /**
     * Retrieves a single savings goal by its ID.
     * @param id The ID of the savings goal to retrieve.
     * @return The savings goal.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> getGoalById(@PathVariable Long id) {
        SavingsGoalResponse goal = savingsGoalService.getSavingsGoalById(id);
        return ResponseEntity.ok(goal);
    }

    /**
     * Updates an existing savings goal.
     * @param id The ID of the savings goal to update.
     * @param request The updated savings goal data.
     * @return The updated savings goal.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> updateGoal(@PathVariable Long id, @Valid @RequestBody SavingsGoalUpdateRequest request) {
        SavingsGoalResponse updatedGoal = savingsGoalService.updateSavingsGoal(id, request);
        return ResponseEntity.ok(updatedGoal);
    }

    /**
     * Deletes a savings goal by its ID.
     * @param id The ID of the savings goal to delete.
     * @return An OK response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {
        savingsGoalService.deleteSavingsGoal(id);
        return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
    }
} 
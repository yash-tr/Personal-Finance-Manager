package com.example.financemanagement.service;

import com.example.financemanagement.dto.SavingsGoalRequest;
import com.example.financemanagement.dto.SavingsGoalResponse;
import com.example.financemanagement.dto.SavingsGoalUpdateRequest;
import com.example.financemanagement.entity.CategoryType;
import com.example.financemanagement.entity.SavingsGoal;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.exception.BadRequestException;
import com.example.financemanagement.exception.ForbiddenException;
import com.example.financemanagement.exception.ResourceNotFoundException;
import com.example.financemanagement.repository.SavingsGoalRepository;
import com.example.financemanagement.repository.TransactionRepository;
import com.example.financemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing savings goals.
 */
@Service
public class SavingsGoalService {

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Creates a new savings goal for the current user.
     * @param request DTO containing the goal details.
     * @return The created savings goal as a DTO, including progress calculation.
     */
    @Transactional
    public SavingsGoalResponse createSavingsGoal(SavingsGoalRequest request) {
        User user = getCurrentUser();
        LocalDate startDate = (request.getStartDate() != null) ? request.getStartDate() : LocalDate.now();
        
        // Validate that start date is not after target date
        if (startDate.isAfter(request.getTargetDate())) {
            throw new BadRequestException("Start date cannot be after target date");
        }
        
        SavingsGoal goal = new SavingsGoal(
                request.getGoalName(),
                request.getTargetAmount(),
                request.getTargetDate(),
                startDate,
                user
        );
        
        SavingsGoal savedGoal = savingsGoalRepository.save(goal);
        return convertToResponse(savedGoal);
    }

    /**
     * Finds all savings goals for the current user.
     * @return A list of savings goal DTOs.
     */
    @Transactional(readOnly = true)
    public List<SavingsGoalResponse> getAllSavingsGoals() {
        User user = getCurrentUser();
        return savingsGoalRepository.findByUserId(user.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Finds a single savings goal by its ID.
     * @param id The ID of the goal to find.
     * @return The savings goal DTO.
     */
    @Transactional(readOnly = true)
    public SavingsGoalResponse getSavingsGoalById(Long id) {
        User user = getCurrentUser();
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found with id: " + id));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to access this savings goal.");
        }
        return convertToResponse(goal);
    }

    /**
     * Updates an existing savings goal.
     * @param id The ID of the goal to update.
     * @param request DTO containing the updated goal details.
     * @return The updated savings goal DTO.
     */
    @Transactional
    public SavingsGoalResponse updateSavingsGoal(Long id, SavingsGoalUpdateRequest request) {
        User user = getCurrentUser();
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found with id: " + id));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to update this savings goal.");
        }

        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }
        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }

        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        return convertToResponse(updatedGoal);
    }

    /**
     * Deletes a savings goal by its ID.
     * @param id The ID of the goal to delete.
     */
    @Transactional
    public void deleteSavingsGoal(Long id) {
        User user = getCurrentUser();
        if (!savingsGoalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Savings goal not found with id: " + id);
        }
        SavingsGoal goal = savingsGoalRepository.findById(id).get();
        if(!goal.getUser().getId().equals(user.getId())){
             throw new ForbiddenException("You are not authorized to delete this goal.");
        }
        savingsGoalRepository.deleteById(id);
    }

    /**
     * Converts a SavingsGoal entity to a DTO, calculating progress along the way.
     * @param goal The entity to convert.
     * @return The corresponding DTO.
     */
    private SavingsGoalResponse convertToResponse(SavingsGoal goal) {
        BigDecimal currentProgress = calculateProgress(goal);
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentProgress);
        BigDecimal progressPercentageBd = BigDecimal.ZERO;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            progressPercentageBd = currentProgress.divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
        }

        double progressPercentage = progressPercentageBd.doubleValue();

        return new SavingsGoalResponse(
                goal.getId(),
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getTargetDate(),
                goal.getStartDate(),
                currentProgress,
                progressPercentage,
                remainingAmount
        );
    }
    
    /**
     * Calculates the current progress towards a savings goal.
     * Progress is defined as (Total Income - Total Expenses) since the goal's start date.
     * @param goal The savings goal for which to calculate progress.
     * @return The current progress as a BigDecimal.
     */
    private BigDecimal calculateProgress(SavingsGoal goal) {
        BigDecimal totalIncome = transactionRepository.calculateTotalAmountByTypeAndDateRange(
                goal.getUser().getId(), CategoryType.INCOME, goal.getStartDate(), LocalDate.now());
        BigDecimal totalExpenses = transactionRepository.calculateTotalAmountByTypeAndDateRange(
                goal.getUser().getId(), CategoryType.EXPENSE, goal.getStartDate(), LocalDate.now());
        
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        return totalIncome.subtract(totalExpenses);
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     * @return The User entity.
     */
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
} 
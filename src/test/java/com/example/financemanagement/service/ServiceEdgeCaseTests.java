package com.example.financemanagement.service;

import com.example.financemanagement.dto.*;
import com.example.financemanagement.entity.*;
import com.example.financemanagement.exception.*;
import com.example.financemanagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceEdgeCaseTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private SavingsGoalRepository savingsGoalRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private CategoryService categoryService;
    @InjectMocks
    private SavingsGoalService savingsGoalService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "password", "Test User", "+1234567890");
        testUser.setId(1L);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    void categoryService_deleteCategoryByName_CategoryNotFound() {
        when(categoryRepository.findByNameAndUserId("NonExistent", 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> categoryService.deleteCategoryByName("NonExistent"));
    }

    @Test
    void categoryService_deleteCategoryByName_NotCustomCategory() {
        Category defaultCategory = new Category("Salary", CategoryType.INCOME, false, testUser);
        when(categoryRepository.findByNameAndUserId("Salary", 1L)).thenReturn(Optional.of(defaultCategory));

        assertThrows(ForbiddenException.class, 
                () -> categoryService.deleteCategoryByName("Salary"));
    }

    @Test
    void categoryService_deleteCategoryByName_CategoryInUse() {
        Category customCategory = new Category("CustomCategory", CategoryType.EXPENSE, true, testUser);
        customCategory.setId(1L);
        when(categoryRepository.findByNameAndUserId("CustomCategory", 1L)).thenReturn(Optional.of(customCategory));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThrows(BadRequestException.class, 
                () -> categoryService.deleteCategoryByName("CustomCategory"));
    }

    @Test
    void categoryService_createCustomCategory_DuplicateName() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("ExistingCategory");
        request.setType(CategoryType.EXPENSE);

        when(categoryRepository.existsByNameAndUserId("ExistingCategory", 1L)).thenReturn(true);

        assertThrows(ResourceConflictException.class, 
                () -> categoryService.createCustomCategory(request));
    }

    @Test
    void savingsGoalService_getSavingsGoalById_NotFound() {
        when(savingsGoalRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> savingsGoalService.getSavingsGoalById(999L));
    }

    @Test
    void savingsGoalService_updateSavingsGoal_NotFound() {
        SavingsGoalUpdateRequest request = new SavingsGoalUpdateRequest();
        request.setTargetAmount(new BigDecimal("6000.00"));

        when(savingsGoalRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> savingsGoalService.updateSavingsGoal(999L, request));
    }

    @Test
    void savingsGoalService_deleteSavingsGoal_NotFound() {
        when(savingsGoalRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> savingsGoalService.deleteSavingsGoal(999L));
    }

    @Test
    void service_getCurrentUser_UserNotFound() {
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            categoryService.findAllByCurrentUser();
        });
    }
} 
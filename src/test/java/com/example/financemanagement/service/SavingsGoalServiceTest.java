package com.example.financemanagement.service;

import com.example.financemanagement.dto.SavingsGoalRequest;
import com.example.financemanagement.dto.SavingsGoalResponse;
import com.example.financemanagement.dto.SavingsGoalUpdateRequest;
import com.example.financemanagement.entity.CategoryType;
import com.example.financemanagement.entity.SavingsGoal;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.exception.ForbiddenException;
import com.example.financemanagement.repository.SavingsGoalRepository;
import com.example.financemanagement.repository.TransactionRepository;
import com.example.financemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsGoalServiceTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SavingsGoalService savingsGoalService;

    private User user;
    private SavingsGoal goal;

    @BeforeEach
    void setUp() {
        user = new User("testuser@example.com", "password", "Test User", "1234567890");
        user.setId(1L);

        goal = new SavingsGoal("Vacation", BigDecimal.valueOf(2000), LocalDate.now().plusYears(1), LocalDate.now(), user);
        goal.setId(1L);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }

    @Test
    void createSavingsGoal_Success() {
        SavingsGoalRequest request = new SavingsGoalRequest();
        request.setGoalName("Vacation");
        request.setTargetAmount(BigDecimal.valueOf(2000));
        request.setTargetDate(LocalDate.now().plusYears(1));

        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(goal);
        when(transactionRepository.calculateTotalAmountByTypeAndDateRange(anyLong(), eq(CategoryType.INCOME), any(), any())).thenReturn(BigDecimal.valueOf(500));
        when(transactionRepository.calculateTotalAmountByTypeAndDateRange(anyLong(), eq(CategoryType.EXPENSE), any(), any())).thenReturn(BigDecimal.valueOf(100));

        SavingsGoalResponse result = savingsGoalService.createSavingsGoal(request);

        assertNotNull(result);
        assertEquals("Vacation", result.getGoalName());
        assertEquals(0, BigDecimal.valueOf(400).compareTo(result.getCurrentProgress()));
    }

    @Test
    void getAllSavingsGoals_Success() {
        when(savingsGoalRepository.findByUserId(user.getId())).thenReturn(Collections.singletonList(goal));

        List<SavingsGoalResponse> result = savingsGoalService.getAllSavingsGoals();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getSavingsGoalById_Success() {
        when(savingsGoalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));

        SavingsGoalResponse result = savingsGoalService.getSavingsGoalById(goal.getId());

        assertNotNull(result);
        assertEquals(goal.getId(), result.getId());
    }

    @Test
    void getSavingsGoalById_Forbidden() {
        User otherUser = new User("other@user.com", "pw", "Other", "123");
        otherUser.setId(2L);
        goal.setUser(otherUser);

        when(savingsGoalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));

        assertThrows(ForbiddenException.class, () -> savingsGoalService.getSavingsGoalById(goal.getId()));
    }

    @Test
    void updateSavingsGoal_Success() {
        SavingsGoalUpdateRequest request = new SavingsGoalUpdateRequest();
        request.setTargetAmount(BigDecimal.valueOf(2500));

        when(savingsGoalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(goal);

        SavingsGoalResponse result = savingsGoalService.updateSavingsGoal(goal.getId(), request);

        assertNotNull(result);
        assertEquals(0, BigDecimal.valueOf(2500).compareTo(result.getTargetAmount()));
    }

    @Test
    void deleteSavingsGoal_Success() {
        when(savingsGoalRepository.existsById(goal.getId())).thenReturn(true);
        when(savingsGoalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        doNothing().when(savingsGoalRepository).deleteById(goal.getId());
        
        assertDoesNotThrow(() -> savingsGoalService.deleteSavingsGoal(goal.getId()));

        verify(savingsGoalRepository, times(1)).deleteById(goal.getId());
    }
} 
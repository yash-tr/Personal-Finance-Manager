package com.example.financemanagement.service;

import com.example.financemanagement.dto.MonthlyReport;
import com.example.financemanagement.dto.YearlyReport;
import com.example.financemanagement.entity.Category;
import com.example.financemanagement.entity.CategoryType;
import com.example.financemanagement.entity.Transaction;
import com.example.financemanagement.entity.User;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportService reportService;

    private User user;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        user = new User("testuser@example.com", "password", "Test User", "1234567890");
        user.setId(1L);

        Category salary = new Category("Salary", CategoryType.INCOME, false, user);
        Category freelance = new Category("Freelance", CategoryType.INCOME, false, user);
        Category food = new Category("Food", CategoryType.EXPENSE, false, user);
        Category rent = new Category("Rent", CategoryType.EXPENSE, false, user);

        transactions = Arrays.asList(
                new Transaction(BigDecimal.valueOf(3000), LocalDate.of(2024, 1, 15), "Salary", CategoryType.INCOME, user, salary),
                new Transaction(BigDecimal.valueOf(500), LocalDate.of(2024, 1, 20), "Freelance", CategoryType.INCOME, user, freelance),
                new Transaction(BigDecimal.valueOf(400), LocalDate.of(2024, 1, 10), "Groceries", CategoryType.EXPENSE, user, food),
                new Transaction(BigDecimal.valueOf(1200), LocalDate.of(2024, 1, 1), "Rent", CategoryType.EXPENSE, user, rent)
        );

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }

    @Test
    void generateMonthlyReport_Success() {
        when(transactionRepository.findByUserIdAndDateRange(eq(user.getId()), any(LocalDate.class), any(LocalDate.class))).thenReturn(transactions);

        MonthlyReport report = reportService.generateMonthlyReport(2024, 1);

        assertNotNull(report);
        assertEquals(2024, report.getYear());
        assertEquals(1, report.getMonth());
        assertEquals(0, new BigDecimal("3000").compareTo(report.getTotalIncome().get("Salary")));
        assertEquals(0, new BigDecimal("500").compareTo(report.getTotalIncome().get("Freelance")));
        assertEquals(0, new BigDecimal("400").compareTo(report.getTotalExpenses().get("Food")));
        assertEquals(0, new BigDecimal("1200").compareTo(report.getTotalExpenses().get("Rent")));
        assertEquals(0, new BigDecimal("1900").compareTo(report.getNetSavings()));
    }

    @Test
    void generateYearlyReport_Success() {
        when(transactionRepository.findByUserIdAndDateRange(eq(user.getId()), any(LocalDate.class), any(LocalDate.class))).thenReturn(transactions);

        YearlyReport report = reportService.generateYearlyReport(2024);

        assertNotNull(report);
        assertEquals(2024, report.getYear());
        assertEquals(0, new BigDecimal("3000").compareTo(report.getTotalIncome().get("Salary")));
        assertEquals(0, new BigDecimal("500").compareTo(report.getTotalIncome().get("Freelance")));
        assertEquals(0, new BigDecimal("400").compareTo(report.getTotalExpenses().get("Food")));
        assertEquals(0, new BigDecimal("1200").compareTo(report.getTotalExpenses().get("Rent")));
        assertEquals(0, new BigDecimal("1900").compareTo(report.getNetSavings()));
    }
} 
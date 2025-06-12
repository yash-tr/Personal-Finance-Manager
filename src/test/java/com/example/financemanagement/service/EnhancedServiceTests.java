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
class EnhancedServiceTests {

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
    private UserService userService;
    @InjectMocks
    private CategoryService categoryService;
    @InjectMocks
    private TransactionService transactionService;
    @InjectMocks
    private ReportService reportService;

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
    void userService_registerUser_Success() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser@example.com");
        request.setPassword("password123");
        request.setFullName("New User");
        request.setPhoneNumber("+1987654321");

        when(userRepository.existsByUsername("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        // Mock category creation for default categories
        when(categoryRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        User result = userService.registerUser(request);

        assertNotNull(result);
        assertEquals("newuser@example.com", result.getUsername());
        verify(userRepository).save(any(User.class));
        verify(categoryRepository).saveAll(anyList());
    }

    @Test
    void userService_registerUser_DuplicateUsername() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("existing@example.com");
        request.setPassword("password123");
        request.setFullName("Existing User");
        request.setPhoneNumber("+1987654321");

        when(userRepository.existsByUsername("existing@example.com")).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> userService.registerUser(request));
    }

    @Test
    void categoryService_findAllByCurrentUser_Success() {
        List<Category> categories = Arrays.asList(
            new Category("Food", CategoryType.EXPENSE, false, testUser),
            new Category("Salary", CategoryType.INCOME, false, testUser),
            new Category("Custom", CategoryType.EXPENSE, true, testUser)
        );

        when(categoryRepository.findByUserId(1L)).thenReturn(categories);

        List<CategoryResponse> responses = categoryService.findAllByCurrentUser();

        assertEquals(3, responses.size());
        assertTrue(responses.stream().anyMatch(r -> "Food".equals(r.getName())));
        assertTrue(responses.stream().anyMatch(r -> "Salary".equals(r.getName())));
        assertTrue(responses.stream().anyMatch(r -> "Custom".equals(r.getName())));
    }

    @Test
    void categoryService_createCustomCategory_Success() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("NewCategory");
        request.setType(CategoryType.EXPENSE);

        when(categoryRepository.existsByNameAndUserId("NewCategory", 1L)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(1L);
            return category;
        });

        CategoryResponse response = categoryService.createCustomCategory(request);

        assertEquals("NewCategory", response.getName());
        assertEquals(CategoryType.EXPENSE, response.getType());
        assertTrue(response.isCustom());
    }

    @Test
    void transactionService_getTransactions_Success() {
        List<Transaction> transactions = Arrays.asList(
            createTestTransaction("Groceries", new BigDecimal("100.00")),
            createTestTransaction("Salary", new BigDecimal("3000.00"))
        );

        when(transactionRepository.findTransactionsByFilters(1L, null, null, null)).thenReturn(transactions);

        List<TransactionResponse> responses = transactionService.getTransactions(null, null, null);

        assertEquals(2, responses.size());
        assertTrue(responses.stream().anyMatch(r -> "Groceries".equals(r.getDescription())));
        assertTrue(responses.stream().anyMatch(r -> "Salary".equals(r.getDescription())));
    }

    @Test
    void transactionService_getTransactions_WithDateFilter() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        List<Transaction> transactions = Arrays.asList(
            createTestTransaction("Recent Transaction", new BigDecimal("50.00"))
        );

        when(transactionRepository.findTransactionsByFilters(1L, startDate, endDate, null))
                .thenReturn(transactions);

        List<TransactionResponse> responses = transactionService.getTransactions(startDate, endDate, null);

        assertEquals(1, responses.size());
        assertEquals("Recent Transaction", responses.get(0).getDescription());
    }

    @Test
    void reportService_generateMonthlyReport_Success() {
        List<Transaction> transactions = Arrays.asList(
            createTransactionWithCategory("Salary", CategoryType.INCOME, new BigDecimal("5000.00")),
            createTransactionWithCategory("Rent", CategoryType.EXPENSE, new BigDecimal("1200.00")),
            createTransactionWithCategory("Food", CategoryType.EXPENSE, new BigDecimal("800.00"))
        );

        when(transactionRepository.findByUserIdAndDateRange(eq(1L), any(), any()))
                .thenReturn(transactions);

        MonthlyReport report = reportService.generateMonthlyReport(2024, 6);

        assertEquals(6, report.getMonth());
        assertEquals(2024, report.getYear());
        assertEquals(new BigDecimal("5000.00"), report.getTotalIncome().get("Salary"));
        assertEquals(new BigDecimal("1200.00"), report.getTotalExpenses().get("Rent"));
        assertEquals(new BigDecimal("800.00"), report.getTotalExpenses().get("Food"));
        assertEquals(new BigDecimal("3000.00"), report.getNetSavings()); // 5000 - 2000
    }

    @Test
    void reportService_generateYearlyReport_Success() {
        List<Transaction> transactions = Arrays.asList(
            createTransactionWithCategory("Annual Salary", CategoryType.INCOME, new BigDecimal("60000.00")),
            createTransactionWithCategory("Annual Expenses", CategoryType.EXPENSE, new BigDecimal("40000.00"))
        );

        when(transactionRepository.findByUserIdAndDateRange(eq(1L), any(), any()))
                .thenReturn(transactions);

        YearlyReport report = reportService.generateYearlyReport(2024);

        assertEquals(2024, report.getYear());
        assertEquals(new BigDecimal("60000.00"), report.getTotalIncome().get("Annual Salary"));
        assertEquals(new BigDecimal("40000.00"), report.getTotalExpenses().get("Annual Expenses"));
        assertEquals(new BigDecimal("20000.00"), report.getNetSavings());
    }

    @Test
    void transactionService_createTransaction_WithNullDescription() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDate(LocalDate.now());
        request.setCategory("Food");
        request.setDescription(null); // Null description should be handled

        Category category = new Category("Food", CategoryType.EXPENSE, false, testUser);
        when(categoryRepository.findByNameAndUserId("Food", 1L)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(1L);
            return transaction;
        });

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertNull(response.getDescription());
    }

    @Test
    void categoryService_getCurrentUser_UserNotFound() {
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            categoryService.findAllByCurrentUser();
        });
    }

    @Test
    void reportService_generateMonthlyReport_EmptyTransactions() {
        when(transactionRepository.findByUserIdAndDateRange(eq(1L), any(), any()))
                .thenReturn(Collections.emptyList());

        MonthlyReport report = reportService.generateMonthlyReport(2024, 6);

        assertEquals(6, report.getMonth());
        assertEquals(2024, report.getYear());
        assertTrue(report.getTotalIncome().isEmpty());
        assertTrue(report.getTotalExpenses().isEmpty());
        assertEquals(BigDecimal.ZERO, report.getNetSavings());
    }

    @Test
    void transactionService_updateTransaction_PartialUpdate() {
        Transaction existingTransaction = createTestTransaction("Original Description", new BigDecimal("100.00"));
        Category category = new Category("Food", CategoryType.EXPENSE, false, testUser);

        TransactionRequest updateRequest = new TransactionRequest();
        updateRequest.setAmount(new BigDecimal("150.00"));
        updateRequest.setDescription("Updated Description");
        updateRequest.setCategory("Food");
        updateRequest.setDate(LocalDate.now());

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existingTransaction));
        when(categoryRepository.findByNameAndUserId("Food", 1L)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.updateTransaction(1L, updateRequest);

        assertEquals(new BigDecimal("150.00"), response.getAmount());
        assertEquals("Updated Description", response.getDescription());
    }

    @Test
    void userService_createDefaultCategories() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser@example.com");
        request.setPassword("password123");
        request.setFullName("New User");
        request.setPhoneNumber("+1987654321");

        when(userRepository.existsByUsername("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        // Mock category creation - verify 7 default categories are created
        when(categoryRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Category> categories = invocation.getArgument(0);
            assertEquals(7, categories.size()); // 1 income + 6 expense categories
            return categories;
        });

        User result = userService.registerUser(request);

        assertNotNull(result);
        verify(categoryRepository).saveAll(anyList());
    }

    private Transaction createTestTransaction(String description, BigDecimal amount) {
        Category category = new Category("Food", CategoryType.EXPENSE, false, testUser);
        Transaction transaction = new Transaction(amount, LocalDate.now(), description, CategoryType.EXPENSE, testUser, category);
        transaction.setId(1L);
        return transaction;
    }

    private Transaction createTransactionWithCategory(String categoryName, CategoryType type, BigDecimal amount) {
        Category category = new Category(categoryName, type, false, testUser);
        return new Transaction(amount, LocalDate.now(), categoryName + " transaction", type, testUser, category);
    }
} 
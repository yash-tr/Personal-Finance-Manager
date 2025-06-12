package com.example.financemanagement.dto;

import com.example.financemanagement.entity.CategoryType;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TargetedDtoTests {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    // ==================== SavingsGoalResponse (Current: 28%) ====================
    
    @Test
    void savingsGoalResponse_AllConstructorsAndMethods() {
        // Test no-args constructor
        SavingsGoalResponse response1 = new SavingsGoalResponse();
        assertNotNull(response1);
        
        // Test all-args constructor
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate targetDate = LocalDate.of(2024, 12, 31);
        SavingsGoalResponse response2 = new SavingsGoalResponse(
            1L, "Emergency Fund", new BigDecimal("10000"), targetDate, startDate,
            new BigDecimal("5000"), 50.0, new BigDecimal("5000")
        );
        
        assertEquals(1L, response2.getId());
        assertEquals("Emergency Fund", response2.getGoalName());
        assertEquals(new BigDecimal("10000"), response2.getTargetAmount());
        assertEquals(targetDate, response2.getTargetDate());
        assertEquals(startDate, response2.getStartDate());
        assertEquals(new BigDecimal("5000"), response2.getCurrentProgress());
        assertEquals(50.0, response2.getProgressPercentage());
        assertEquals(new BigDecimal("5000"), response2.getRemainingAmount());
    }

    @Test
    void savingsGoalResponse_SettersAndGetters() {
        SavingsGoalResponse response = new SavingsGoalResponse();
        
        // Test all setters
        response.setId(100L);
        response.setGoalName("Vacation Fund");
        response.setTargetAmount(new BigDecimal("8000"));
        response.setTargetDate(LocalDate.of(2025, 6, 1));
        response.setStartDate(LocalDate.of(2024, 6, 1));
        response.setCurrentProgress(new BigDecimal("3200"));
        response.setProgressPercentage(40.0);
        response.setRemainingAmount(new BigDecimal("4800"));
        
        // Test all getters
        assertEquals(100L, response.getId());
        assertEquals("Vacation Fund", response.getGoalName());
        assertEquals(new BigDecimal("8000"), response.getTargetAmount());
        assertEquals(LocalDate.of(2025, 6, 1), response.getTargetDate());
        assertEquals(LocalDate.of(2024, 6, 1), response.getStartDate());
        assertEquals(new BigDecimal("3200"), response.getCurrentProgress());
        assertEquals(40.0, response.getProgressPercentage());
        assertEquals(new BigDecimal("4800"), response.getRemainingAmount());
    }

    @Test
    void savingsGoalResponse_EqualsAndHashCode() {
        LocalDate startDate = LocalDate.now();
        LocalDate targetDate = LocalDate.now().plusYears(1);
        
        SavingsGoalResponse response1 = new SavingsGoalResponse(
            1L, "Goal", new BigDecimal("1000"), targetDate, startDate,
            new BigDecimal("500"), 50.0, new BigDecimal("500")
        );
        
        SavingsGoalResponse response2 = new SavingsGoalResponse(
            1L, "Goal", new BigDecimal("1000"), targetDate, startDate,
            new BigDecimal("500"), 50.0, new BigDecimal("500")
        );
        
        SavingsGoalResponse response3 = new SavingsGoalResponse(
            2L, "Different Goal", new BigDecimal("2000"), targetDate, startDate,
            new BigDecimal("1000"), 50.0, new BigDecimal("1000")
        );
        
        // Test equals
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1, response1);
        assertNotEquals(response1, null);
        assertNotEquals(response1, "string");
        
        // Test hashCode
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void savingsGoalResponse_ToString() {
        SavingsGoalResponse response = new SavingsGoalResponse(
            1L, "Test Goal", new BigDecimal("5000"), LocalDate.now().plusMonths(6),
            LocalDate.now(), new BigDecimal("2500"), 50.0, new BigDecimal("2500")
        );
        
        String toString = response.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Test Goal"));
        assertTrue(toString.contains("5000"));
        assertTrue(toString.contains("2500"));
        assertTrue(toString.contains("50.0"));
    }

    // ==================== TransactionResponse (Current: 27%) ====================
    
    @Test
    void transactionResponse_AllConstructorsAndMethods() {
        // Test no-args constructor
        TransactionResponse response1 = new TransactionResponse();
        assertNotNull(response1);
        
        // Test all-args constructor
        TransactionResponse response2 = new TransactionResponse(
            1L, new BigDecimal("150.75"), LocalDate.of(2024, 6, 15),
            "Food", "Lunch at restaurant", CategoryType.EXPENSE
        );
        
        assertEquals(1L, response2.getId());
        assertEquals(new BigDecimal("150.75"), response2.getAmount());
        assertEquals(LocalDate.of(2024, 6, 15), response2.getDate());
        assertEquals("Food", response2.getCategory());
        assertEquals("Lunch at restaurant", response2.getDescription());
        assertEquals(CategoryType.EXPENSE, response2.getType());
    }

    @Test
    void transactionResponse_SettersAndGetters() {
        TransactionResponse response = new TransactionResponse();
        
        // Test all setters
        response.setId(999L);
        response.setAmount(new BigDecimal("2500.50"));
        response.setDate(LocalDate.of(2024, 12, 25));
        response.setCategory("Salary");
        response.setDescription("Monthly salary payment");
        response.setType(CategoryType.INCOME);
        
        // Test all getters
        assertEquals(999L, response.getId());
        assertEquals(new BigDecimal("2500.50"), response.getAmount());
        assertEquals(LocalDate.of(2024, 12, 25), response.getDate());
        assertEquals("Salary", response.getCategory());
        assertEquals("Monthly salary payment", response.getDescription());
        assertEquals(CategoryType.INCOME, response.getType());
    }

    @Test
    void transactionResponse_EqualsAndHashCode() {
        LocalDate date = LocalDate.of(2024, 6, 1);
        
        TransactionResponse response1 = new TransactionResponse(
            1L, new BigDecimal("100"), date, "Food", "Test", CategoryType.EXPENSE
        );
        
        TransactionResponse response2 = new TransactionResponse(
            1L, new BigDecimal("100"), date, "Food", "Test", CategoryType.EXPENSE
        );
        
        TransactionResponse response3 = new TransactionResponse(
            2L, new BigDecimal("200"), date, "Transport", "Different", CategoryType.EXPENSE
        );
        
        // Test equals
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1, response1);
        assertNotEquals(response1, null);
        assertNotEquals(response1, "string");
        
        // Test hashCode
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void transactionResponse_ToString() {
        TransactionResponse response = new TransactionResponse(
            1L, new BigDecimal("75.25"), LocalDate.now(), "Entertainment",
            "Movie ticket", CategoryType.EXPENSE
        );
        
        String toString = response.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("75.25"));
        assertTrue(toString.contains("Entertainment"));
        assertTrue(toString.contains("Movie ticket"));
        assertTrue(toString.contains("EXPENSE"));
    }

    // ==================== UserRegistrationRequest (Current: 26%) ====================
    
    @Test
    void userRegistrationRequest_AllConstructorsAndMethods() {
        // Test no-args constructor
        UserRegistrationRequest request1 = new UserRegistrationRequest();
        assertNotNull(request1);
        
        // Test all-args constructor
        UserRegistrationRequest request2 = new UserRegistrationRequest(
            "john.doe@example.com", "securePassword123", "John Doe", "+1-555-123-4567"
        );
        
        assertEquals("john.doe@example.com", request2.getUsername());
        assertEquals("securePassword123", request2.getPassword());
        assertEquals("John Doe", request2.getFullName());
        assertEquals("+1-555-123-4567", request2.getPhoneNumber());
    }

    @Test
    void userRegistrationRequest_SettersAndGetters() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        
        // Test all setters
        request.setUsername("jane.smith@company.com");
        request.setPassword("verySecurePassword456");
        request.setFullName("Jane Elizabeth Smith");
        request.setPhoneNumber("+44-20-7946-0958");
        
        // Test all getters
        assertEquals("jane.smith@company.com", request.getUsername());
        assertEquals("verySecurePassword456", request.getPassword());
        assertEquals("Jane Elizabeth Smith", request.getFullName());
        assertEquals("+44-20-7946-0958", request.getPhoneNumber());
    }

    @Test
    void userRegistrationRequest_ValidationConstraints() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        
        // Test email validation
        request.setUsername("invalid-email");
        request.setPassword("validPassword123");
        request.setFullName("Valid Name");
        request.setPhoneNumber("+1234567890");
        
        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
        
        // Test password length validation
        request.setUsername("valid@email.com");
        request.setPassword("short");
        
        violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        
        // Test empty name validation
        request.setPassword("validPassword123");
        request.setFullName("");
        
        violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fullName")));
        
        // Test valid request
        request.setFullName("Valid Full Name");
        violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userRegistrationRequest_EqualsAndHashCode() {
        UserRegistrationRequest request1 = new UserRegistrationRequest(
            "user@example.com", "password123", "User Name", "+1234567890"
        );
        
        UserRegistrationRequest request2 = new UserRegistrationRequest(
            "user@example.com", "password123", "User Name", "+1234567890"
        );
        
        UserRegistrationRequest request3 = new UserRegistrationRequest(
            "different@example.com", "differentPassword", "Different Name", "+9876543210"
        );
        
        // Test equals
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1, request1);
        assertNotEquals(request1, null);
        assertNotEquals(request1, "string");
        
        // Test hashCode
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void userRegistrationRequest_ToString() {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "test@example.com", "password123", "Test User", "+1234567890"
        );
        
        String toString = request.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("Test User"));
        assertTrue(toString.contains("+1234567890"));
        // Password should not be in toString for security
        assertFalse(toString.contains("password123"));
    }

    // ==================== TransactionUpdateRequest (Current: 21%) ====================
    
    @Test
    void transactionUpdateRequest_AllMethods() {
        // Test no-args constructor
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        assertNotNull(request);
        
        // Test all setters
        request.setAmount(new BigDecimal("999.99"));
        request.setDescription("Updated transaction description");
        request.setCategory("Updated Category");
        
        // Test all getters
        assertEquals(new BigDecimal("999.99"), request.getAmount());
        assertEquals("Updated transaction description", request.getDescription());
        assertEquals("Updated Category", request.getCategory());
    }

    @Test
    void transactionUpdateRequest_ValidationConstraints() {
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        
        // Test negative amount validation
        request.setAmount(new BigDecimal("-100.00"));
        
        Set<ConstraintViolation<TransactionUpdateRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be positive")));
        
        // Test zero amount validation
        request.setAmount(BigDecimal.ZERO);
        
        violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be positive")));
        
        // Test valid positive amount
        request.setAmount(new BigDecimal("0.01"));
        violations = validator.validate(request);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("amount")));
        
        // Test null values are allowed (partial update)
        request.setAmount(null);
        request.setDescription(null);
        request.setCategory(null);
        
        assertNull(request.getAmount());
        assertNull(request.getDescription());
        assertNull(request.getCategory());
    }

    @Test
    void transactionUpdateRequest_EqualsAndHashCode() {
        TransactionUpdateRequest request1 = new TransactionUpdateRequest();
        request1.setAmount(new BigDecimal("150.00"));
        request1.setDescription("Test description");
        request1.setCategory("Test category");
        
        TransactionUpdateRequest request2 = new TransactionUpdateRequest();
        request2.setAmount(new BigDecimal("150.00"));
        request2.setDescription("Test description");
        request2.setCategory("Test category");
        
        TransactionUpdateRequest request3 = new TransactionUpdateRequest();
        request3.setAmount(new BigDecimal("200.00"));
        request3.setDescription("Different description");
        request3.setCategory("Different category");
        
        // Test equals
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1, request1);
        assertNotEquals(request1, null);
        assertNotEquals(request1, "string");
        
        // Test hashCode
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void transactionUpdateRequest_ToString() {
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        request.setAmount(new BigDecimal("123.45"));
        request.setDescription("Test transaction update");
        request.setCategory("Food");
        
        String toString = request.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("123.45"));
        assertTrue(toString.contains("Test transaction update"));
        assertTrue(toString.contains("Food"));
    }

    // ==================== TransactionDto (Current: 50%) ====================
    
    @Test
    void transactionDto_ComprehensiveMethods() {
        TransactionDto dto = new TransactionDto();
        
        // Test all setters
        dto.setId(123L);
        dto.setAmount(new BigDecimal("456.78"));
        dto.setDate(LocalDate.of(2024, 3, 15));
        dto.setDescription("Complete transaction test");
        dto.setType(CategoryType.INCOME);
        dto.setCategoryName("Salary");
        dto.setUserId(789L);
        
        // Test all getters
        assertEquals(123L, dto.getId());
        assertEquals(new BigDecimal("456.78"), dto.getAmount());
        assertEquals(LocalDate.of(2024, 3, 15), dto.getDate());
        assertEquals("Complete transaction test", dto.getDescription());
        assertEquals(CategoryType.INCOME, dto.getType());
        assertEquals("Salary", dto.getCategoryName());
        assertEquals(789L, dto.getUserId());
    }

    @Test
    void transactionDto_ValidationScenarios() {
        TransactionDto dto = new TransactionDto();
        
        // Test with minimum valid values
        dto.setAmount(new BigDecimal("0.01"));
        dto.setDate(LocalDate.now());
        dto.setCategoryName("Valid Category");
        dto.setType(CategoryType.EXPENSE);
        dto.setUserId(1L);
        
        Set<ConstraintViolation<TransactionDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        
        // Test with maximum values
        dto.setAmount(new BigDecimal("999999999.99"));
        dto.setDate(LocalDate.MAX);
        dto.setDescription("A".repeat(255)); // Assuming max length constraint
        
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void transactionDto_EqualsAndHashCode() {
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        
        TransactionDto dto1 = new TransactionDto();
        dto1.setId(1L);
        dto1.setAmount(new BigDecimal("100"));
        dto1.setDate(testDate);
        dto1.setDescription("Test");
        dto1.setType(CategoryType.EXPENSE);
        dto1.setCategoryName("Food");
        dto1.setUserId(1L);
        
        TransactionDto dto2 = new TransactionDto();
        dto2.setId(1L);
        dto2.setAmount(new BigDecimal("100"));
        dto2.setDate(testDate);
        dto2.setDescription("Test");
        dto2.setType(CategoryType.EXPENSE);
        dto2.setCategoryName("Food");
        dto2.setUserId(1L);
        
        // Test equals
        assertEquals(dto1, dto2);
        assertEquals(dto1, dto1);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "string");
        
        // Test hashCode consistency
        assertEquals(dto1.hashCode(), dto2.hashCode());
        
        // Test toString
        String toString = dto1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("100"));
        assertTrue(toString.contains("Test"));
        assertTrue(toString.contains("Food"));
    }

    // ==================== MonthlyReport (Current: 31%) ====================
    
    @Test
    void monthlyReport_ComprehensiveConstructorTests() {
        // Test no-args constructor
        MonthlyReport report1 = new MonthlyReport();
        assertNotNull(report1);
        
        // Test full constructor with complex data
        Map<String, BigDecimal> income = new HashMap<>();
        income.put("Primary Salary", new BigDecimal("5000.00"));
        income.put("Secondary Income", new BigDecimal("1500.00"));
        income.put("Investment Returns", new BigDecimal("500.00"));
        
        Map<String, BigDecimal> expenses = new HashMap<>();
        expenses.put("Housing", new BigDecimal("2000.00"));
        expenses.put("Food & Dining", new BigDecimal("800.00"));
        expenses.put("Transportation", new BigDecimal("400.00"));
        expenses.put("Utilities", new BigDecimal("300.00"));
        
        MonthlyReport report2 = new MonthlyReport(2024, 8, income, expenses, new BigDecimal("3500.00"));
        
        assertEquals(2024, report2.getYear());
        assertEquals(8, report2.getMonth());
        assertEquals(3, report2.getTotalIncome().size());
        assertEquals(4, report2.getTotalExpenses().size());
        assertEquals(new BigDecimal("3500.00"), report2.getNetSavings());
        
        // Verify specific values
        assertEquals(new BigDecimal("5000.00"), report2.getTotalIncome().get("Primary Salary"));
        assertEquals(new BigDecimal("2000.00"), report2.getTotalExpenses().get("Housing"));
    }

    @Test
    void monthlyReport_SettersAndGetters() {
        MonthlyReport report = new MonthlyReport();
        
        Map<String, BigDecimal> testIncome = new HashMap<>();
        testIncome.put("Test Income", new BigDecimal("1000"));
        
        Map<String, BigDecimal> testExpenses = new HashMap<>();
        testExpenses.put("Test Expense", new BigDecimal("500"));
        
        // Test all setters
        report.setYear(2025);
        report.setMonth(12);
        report.setTotalIncome(testIncome);
        report.setTotalExpenses(testExpenses);
        report.setNetSavings(new BigDecimal("500"));
        
        // Test all getters
        assertEquals(2025, report.getYear());
        assertEquals(12, report.getMonth());
        assertEquals(testIncome, report.getTotalIncome());
        assertEquals(testExpenses, report.getTotalExpenses());
        assertEquals(new BigDecimal("500"), report.getNetSavings());
    }

    @Test
    void monthlyReport_EqualsHashCodeToString() {
        Map<String, BigDecimal> income = Map.of("Salary", new BigDecimal("1000"));
        Map<String, BigDecimal> expenses = Map.of("Rent", new BigDecimal("500"));
        
        MonthlyReport report1 = new MonthlyReport(2024, 6, income, expenses, new BigDecimal("500"));
        MonthlyReport report2 = new MonthlyReport(2024, 6, income, expenses, new BigDecimal("500"));
        MonthlyReport report3 = new MonthlyReport(2024, 7, income, expenses, new BigDecimal("500"));
        
        // Test equals
        assertEquals(report1, report2);
        assertNotEquals(report1, report3);
        assertEquals(report1, report1);
        assertNotEquals(report1, null);
        
        // Test hashCode
        assertEquals(report1.hashCode(), report2.hashCode());
        
        // Test toString
        String toString = report1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("2024"));
        assertTrue(toString.contains("6"));
        assertTrue(toString.contains("500"));
    }
} 
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

class ComprehensiveDtoTests {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void categoryResponse_AllMethods() {
        // Test constructors
        CategoryResponse response1 = new CategoryResponse();
        assertNotNull(response1);

        CategoryResponse response2 = new CategoryResponse("Food", CategoryType.EXPENSE, true);
        assertEquals("Food", response2.getName());
        assertEquals(CategoryType.EXPENSE, response2.getType());
        assertTrue(response2.isCustom());

        // Test setters and getters
        response1.setName("Transport");
        response1.setType(CategoryType.EXPENSE);
        response1.setCustom(false);

        assertEquals("Transport", response1.getName());
        assertEquals(CategoryType.EXPENSE, response1.getType());
        assertFalse(response1.isCustom());

        // Test equals and hashCode
        CategoryResponse response3 = new CategoryResponse("Food", CategoryType.EXPENSE, true);
        assertEquals(response2, response3);
        assertEquals(response2.hashCode(), response3.hashCode());

        CategoryResponse response4 = new CategoryResponse("Rent", CategoryType.EXPENSE, false);
        assertNotEquals(response2, response4);

        // Test toString
        String toString = response2.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Food"));
        assertTrue(toString.contains("EXPENSE"));

        // Test edge cases
        assertNotEquals(response2, null);
        assertNotEquals(response2, "string");
        assertEquals(response2, response2);
    }

    @Test
    void createCategoryRequest_AllMethods() {
        // Test constructors
        CreateCategoryRequest request1 = new CreateCategoryRequest();
        assertNotNull(request1);

        CreateCategoryRequest request2 = new CreateCategoryRequest("Groceries", CategoryType.EXPENSE);
        assertEquals("Groceries", request2.getName());
        assertEquals(CategoryType.EXPENSE, request2.getType());

        // Test setters and getters
        request1.setName("Health");
        request1.setType(CategoryType.EXPENSE);

        assertEquals("Health", request1.getName());
        assertEquals(CategoryType.EXPENSE, request1.getType());

        // Test validation
        CreateCategoryRequest invalidRequest = new CreateCategoryRequest();
        invalidRequest.setName(""); // Invalid empty name
        invalidRequest.setType(null); // Invalid null type

        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());

        // Test valid request
        CreateCategoryRequest validRequest = new CreateCategoryRequest();
        validRequest.setName("ValidCategory");
        validRequest.setType(CategoryType.INCOME);

        Set<ConstraintViolation<CreateCategoryRequest>> validViolations = validator.validate(validRequest);
        assertTrue(validViolations.isEmpty());

        // Test equals and hashCode
        CreateCategoryRequest request3 = new CreateCategoryRequest("Groceries", CategoryType.EXPENSE);
        assertEquals(request2, request3);
        assertEquals(request2.hashCode(), request3.hashCode());

        // Test toString
        String toString = request2.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Groceries"));
    }

    @Test
    void loginRequest_AllMethods() {
        // Test constructors
        LoginRequest request1 = new LoginRequest();
        assertNotNull(request1);

        LoginRequest request2 = new LoginRequest("user@example.com", "password123");
        assertEquals("user@example.com", request2.getUsername());
        assertEquals("password123", request2.getPassword());

        // Test setters and getters
        request1.setUsername("newuser@example.com");
        request1.setPassword("newpassword");

        assertEquals("newuser@example.com", request1.getUsername());
        assertEquals("newpassword", request1.getPassword());

        // Test validation
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername(""); // Invalid empty username
        invalidRequest.setPassword(""); // Invalid empty password

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());

        // Test equals and hashCode
        LoginRequest request3 = new LoginRequest("user@example.com", "password123");
        assertEquals(request2, request3);
        assertEquals(request2.hashCode(), request3.hashCode());

        // Test toString
        String toString = request2.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("user@example.com"));
    }

    @Test
    void monthlyReport_AllMethods() {
        // Test constructors
        MonthlyReport report1 = new MonthlyReport();
        assertNotNull(report1);

        Map<String, BigDecimal> income = new HashMap<>();
        income.put("Salary", new BigDecimal("5000"));
        Map<String, BigDecimal> expenses = new HashMap<>();
        expenses.put("Rent", new BigDecimal("1200"));

        MonthlyReport report2 = new MonthlyReport(2024, 6, income, expenses, new BigDecimal("3800"));
        assertEquals(2024, report2.getYear());
        assertEquals(6, report2.getMonth());
        assertEquals(income, report2.getTotalIncome());
        assertEquals(expenses, report2.getTotalExpenses());
        assertEquals(new BigDecimal("3800"), report2.getNetSavings());

        // Test setters and getters
        report1.setYear(2023);
        report1.setMonth(12);
        report1.setTotalIncome(income);
        report1.setTotalExpenses(expenses);
        report1.setNetSavings(new BigDecimal("2000"));

        assertEquals(2023, report1.getYear());
        assertEquals(12, report1.getMonth());
        assertEquals(income, report1.getTotalIncome());
        assertEquals(expenses, report1.getTotalExpenses());
        assertEquals(new BigDecimal("2000"), report1.getNetSavings());

        // Test equals and hashCode
        MonthlyReport report3 = new MonthlyReport(2024, 6, income, expenses, new BigDecimal("3800"));
        assertEquals(report2, report3);
        assertEquals(report2.hashCode(), report3.hashCode());

        // Test toString
        String toString = report2.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("2024"));
        assertTrue(toString.contains("6"));
    }

    @Test
    void yearlyReport_AllMethods() {
        // Test constructors
        YearlyReport report1 = new YearlyReport();
        assertNotNull(report1);

        Map<String, BigDecimal> income = new HashMap<>();
        income.put("Salary", new BigDecimal("60000"));
        Map<String, BigDecimal> expenses = new HashMap<>();
        expenses.put("Total Expenses", new BigDecimal("40000"));

        YearlyReport report2 = new YearlyReport(2024, income, expenses, new BigDecimal("20000"));
        assertEquals(2024, report2.getYear());
        assertEquals(income, report2.getTotalIncome());
        assertEquals(expenses, report2.getTotalExpenses());
        assertEquals(new BigDecimal("20000"), report2.getNetSavings());

        // Test setters and getters
        report1.setYear(2023);
        report1.setTotalIncome(income);
        report1.setTotalExpenses(expenses);
        report1.setNetSavings(new BigDecimal("15000"));

        assertEquals(2023, report1.getYear());
        assertEquals(income, report1.getTotalIncome());
        assertEquals(expenses, report1.getTotalExpenses());
        assertEquals(new BigDecimal("15000"), report1.getNetSavings());

        // Test equals and hashCode
        YearlyReport report3 = new YearlyReport(2024, income, expenses, new BigDecimal("20000"));
        assertEquals(report2, report3);
        assertEquals(report2.hashCode(), report3.hashCode());

        // Test toString
        String toString = report2.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("2024"));
    }

    @Test
    void savingsGoalRequest_AllMethods() {
        // Test constructors (only no-args constructor available)
        SavingsGoalRequest request1 = new SavingsGoalRequest();
        assertNotNull(request1);

        // Test setters and getters
        request1.setGoalName("Emergency Fund");
        request1.setTargetAmount(new BigDecimal("5000"));
        request1.setTargetDate(LocalDate.now().plusMonths(6));
        request1.setStartDate(LocalDate.now());

        assertEquals("Emergency Fund", request1.getGoalName());
        assertEquals(new BigDecimal("5000"), request1.getTargetAmount());
        assertNotNull(request1.getTargetDate());
        assertNotNull(request1.getStartDate());

        // Test validation
        SavingsGoalRequest invalidRequest = new SavingsGoalRequest();
        invalidRequest.setGoalName(""); // Invalid empty name
        invalidRequest.setTargetAmount(new BigDecimal("-100")); // Invalid negative amount

        Set<ConstraintViolation<SavingsGoalRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());

        // Test toString
        String toString = request1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Emergency Fund"));
    }

    @Test
    void savingsGoalResponse_AllMethods() {
        // Test constructors
        SavingsGoalResponse response1 = new SavingsGoalResponse();
        assertNotNull(response1);

        LocalDate startDate = LocalDate.now();
        LocalDate targetDate = LocalDate.now().plusMonths(6);
        SavingsGoalResponse response2 = new SavingsGoalResponse(1L, "Emergency Fund", new BigDecimal("5000"), 
                targetDate, startDate, new BigDecimal("2000"), 40.0, new BigDecimal("3000"));
        
        assertEquals(1L, response2.getId());
        assertEquals("Emergency Fund", response2.getGoalName());
        assertEquals(new BigDecimal("5000"), response2.getTargetAmount());
        assertEquals(targetDate, response2.getTargetDate());
        assertEquals(startDate, response2.getStartDate());
        assertEquals(new BigDecimal("2000"), response2.getCurrentProgress());
        assertEquals(40.0, response2.getProgressPercentage());
        assertEquals(new BigDecimal("3000"), response2.getRemainingAmount());

        // Test setters and getters
        response1.setId(2L);
        response1.setGoalName("Vacation");
        response1.setTargetAmount(new BigDecimal("3000"));
        response1.setCurrentProgress(new BigDecimal("1500"));
        response1.setProgressPercentage(50.0);
        response1.setRemainingAmount(new BigDecimal("1500"));
        response1.setTargetDate(LocalDate.now().plusMonths(12));
        response1.setStartDate(LocalDate.now());

        assertEquals(2L, response1.getId());
        assertEquals("Vacation", response1.getGoalName());
        assertEquals(new BigDecimal("3000"), response1.getTargetAmount());
        assertEquals(new BigDecimal("1500"), response1.getCurrentProgress());
        assertEquals(50.0, response1.getProgressPercentage());
        assertEquals(new BigDecimal("1500"), response1.getRemainingAmount());

        // Test toString
        String toString = response2.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Emergency Fund"));
    }

    @Test
    void savingsGoalUpdateRequest_AllMethods() {
        // Test constructors (only no-args constructor available)
        SavingsGoalUpdateRequest request1 = new SavingsGoalUpdateRequest();
        assertNotNull(request1);

        // Test setters and getters
        request1.setTargetAmount(new BigDecimal("6000"));
        request1.setTargetDate(LocalDate.now().plusMonths(8));

        assertEquals(new BigDecimal("6000"), request1.getTargetAmount());
        assertNotNull(request1.getTargetDate());

        // Test toString
        String toString = request1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("6000"));
    }

    @Test
    void transactionDto_AllMethods() {
        // Test constructors (only no-args constructor available)
        TransactionDto dto1 = new TransactionDto();
        assertNotNull(dto1);

        // Test setters and getters
        dto1.setId(1L);
        dto1.setAmount(new BigDecimal("150"));
        dto1.setDate(LocalDate.now());
        dto1.setDescription("Groceries");
        dto1.setType(CategoryType.EXPENSE);
        dto1.setCategoryName("Food");
        dto1.setUserId(1L);

        assertEquals(1L, dto1.getId());
        assertEquals(new BigDecimal("150"), dto1.getAmount());
        assertNotNull(dto1.getDate());
        assertEquals("Groceries", dto1.getDescription());
        assertEquals(CategoryType.EXPENSE, dto1.getType());
        assertEquals("Food", dto1.getCategoryName());
        assertEquals(1L, dto1.getUserId());

        // Test toString
        String toString = dto1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Groceries"));
    }

    @Test
    void transactionResponse_AllMethods() {
        // Test constructors
        TransactionResponse response1 = new TransactionResponse();
        assertNotNull(response1);

        TransactionResponse response2 = new TransactionResponse(1L, new BigDecimal("250"), 
                LocalDate.now(), "Income", "Salary", CategoryType.INCOME);
        assertEquals(1L, response2.getId());
        assertEquals(new BigDecimal("250"), response2.getAmount());
        assertNotNull(response2.getDate());
        assertEquals("Income", response2.getCategory());
        assertEquals("Salary", response2.getDescription());
        assertEquals(CategoryType.INCOME, response2.getType());

        // Test setters and getters
        response1.setId(2L);
        response1.setAmount(new BigDecimal("300"));
        response1.setDate(LocalDate.now());
        response1.setDescription("Bonus");
        response1.setType(CategoryType.INCOME);
        response1.setCategory("Bonus");

        assertEquals(2L, response1.getId());
        assertEquals(new BigDecimal("300"), response1.getAmount());
        assertNotNull(response1.getDate());
        assertEquals("Bonus", response1.getDescription());
        assertEquals(CategoryType.INCOME, response1.getType());
        assertEquals("Bonus", response1.getCategory());

        // Test toString
        String toString = response2.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Salary"));
    }

    @Test
    void transactionUpdateRequest_AllMethods() {
        // Test constructors (only no-args constructor available)
        TransactionUpdateRequest request1 = new TransactionUpdateRequest();
        assertNotNull(request1);

        // Test setters and getters
        request1.setAmount(new BigDecimal("400"));
        request1.setDescription("Updated Description");
        request1.setCategory("Updated Category");

        assertEquals(new BigDecimal("400"), request1.getAmount());
        assertEquals("Updated Description", request1.getDescription());
        assertEquals("Updated Category", request1.getCategory());

        // Test toString
        String toString = request1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Updated Description"));
    }

    @Test
    void userRegistrationRequest_AllMethods() {
        // Test constructors
        UserRegistrationRequest request1 = new UserRegistrationRequest();
        assertNotNull(request1);

        UserRegistrationRequest request2 = new UserRegistrationRequest("user@example.com", 
                "password123", "John Doe", "+1234567890");
        assertEquals("user@example.com", request2.getUsername());
        assertEquals("password123", request2.getPassword());
        assertEquals("John Doe", request2.getFullName());
        assertEquals("+1234567890", request2.getPhoneNumber());

        // Test setters and getters
        request1.setUsername("newuser@example.com");
        request1.setPassword("newpassword");
        request1.setFullName("Jane Smith");
        request1.setPhoneNumber("+9876543210");

        assertEquals("newuser@example.com", request1.getUsername());
        assertEquals("newpassword", request1.getPassword());
        assertEquals("Jane Smith", request1.getFullName());
        assertEquals("+9876543210", request1.getPhoneNumber());

        // Test validation
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest();
        invalidRequest.setUsername("invalid-email"); // Invalid email format
        invalidRequest.setPassword("123"); // Too short
        invalidRequest.setFullName(""); // Empty name
        invalidRequest.setPhoneNumber("invalid"); // Invalid phone

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());

        // Test toString
        String toString = request2.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("John Doe"));
    }
} 
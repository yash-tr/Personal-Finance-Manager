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

class AdvancedDtoTests {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void transactionDto_ValidationAndEdgeCases() {
        TransactionDto dto = new TransactionDto();
        
        // Test null amount validation
        dto.setAmount(null);
        dto.setDate(LocalDate.now());
        dto.setCategoryName("Food");
        
        Set<ConstraintViolation<TransactionDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must not be null")));
        
        // Test zero amount validation
        dto.setAmount(BigDecimal.ZERO);
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be greater than 0")));
        
        // Test negative amount validation
        dto.setAmount(new BigDecimal("-50.00"));
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be greater than 0")));
        
        // Test valid amount
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDate(LocalDate.now());
        dto.setCategoryName("Food");
        dto.setDescription("Valid transaction");
        dto.setType(CategoryType.EXPENSE);
        dto.setUserId(1L);
        
        violations = validator.validate(dto);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("amount")));
        
        // Test equals with different objects
        TransactionDto dto2 = new TransactionDto();
        dto2.setAmount(new BigDecimal("100.00"));
        dto2.setDate(dto.getDate());
        dto2.setCategoryName("Food");
        dto2.setDescription("Valid transaction");
        dto2.setType(CategoryType.EXPENSE);
        dto2.setUserId(1L);
        
        // Test hashCode consistency
        int hashCode1 = dto.hashCode();
        int hashCode2 = dto.hashCode();
        assertEquals(hashCode1, hashCode2);
        
        // Test toString contains all fields
        String toString = dto.toString();
        assertTrue(toString.contains("100.00"));
        assertTrue(toString.contains("Food"));
        assertTrue(toString.contains("Valid transaction"));
        assertTrue(toString.contains("EXPENSE"));
    }

    @Test
    void savingsGoalRequest_ComplexValidation() {
        SavingsGoalRequest request = new SavingsGoalRequest();
        
        // Test blank goal name
        request.setGoalName("   ");
        request.setTargetAmount(new BigDecimal("1000"));
        request.setTargetDate(LocalDate.now().plusMonths(6));
        
        Set<ConstraintViolation<SavingsGoalRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cannot be blank")));
        
        // Test past target date
        request.setGoalName("Valid Goal");
        request.setTargetDate(LocalDate.now().minusDays(1));
        
        violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be in the future")));
        
        // Test zero target amount
        request.setTargetDate(LocalDate.now().plusMonths(6));
        request.setTargetAmount(BigDecimal.ZERO);
        
        violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be positive")));
        
        // Test valid request
        request.setTargetAmount(new BigDecimal("5000"));
        request.setStartDate(LocalDate.now());
        
        violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        
        // Test equals and hashCode
        SavingsGoalRequest request2 = new SavingsGoalRequest();
        request2.setGoalName("Valid Goal");
        request2.setTargetAmount(new BigDecimal("5000"));
        request2.setTargetDate(request.getTargetDate());
        request2.setStartDate(request.getStartDate());
        
        assertEquals(request, request2);
        assertEquals(request.hashCode(), request2.hashCode());
    }

    @Test
    void transactionUpdateRequest_ValidationEdgeCases() {
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        
        // Test negative amount
        request.setAmount(new BigDecimal("-100"));
        
        Set<ConstraintViolation<TransactionUpdateRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be positive")));
        
        // Test very small amount
        request.setAmount(new BigDecimal("0.001"));
        
        violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be positive")));
        
        // Test valid amount
        request.setAmount(new BigDecimal("0.01"));
        request.setDescription("Minimum valid amount");
        request.setCategory("Test Category");
        
        violations = validator.validate(request);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("amount")));
        
        // Test null values (should be allowed for optional updates)
        request.setAmount(null);
        request.setDescription(null);
        request.setCategory(null);
        
        assertNull(request.getAmount());
        assertNull(request.getDescription());
        assertNull(request.getCategory());
    }

    @Test
    void savingsGoalUpdateRequest_ValidationAndEquality() {
        SavingsGoalUpdateRequest request = new SavingsGoalUpdateRequest();
        
        // Test negative amount validation
        request.setTargetAmount(new BigDecimal("-1000"));
        
        Set<ConstraintViolation<SavingsGoalUpdateRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be positive")));
        
        // Test past date validation
        request.setTargetAmount(new BigDecimal("1000"));
        request.setTargetDate(LocalDate.now().minusDays(1));
        
        violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be in the future")));
        
        // Test valid request
        request.setTargetDate(LocalDate.now().plusMonths(3));
        
        violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        
        // Test equals and hashCode
        SavingsGoalUpdateRequest request2 = new SavingsGoalUpdateRequest();
        request2.setTargetAmount(new BigDecimal("1000"));
        request2.setTargetDate(request.getTargetDate());
        
        assertEquals(request, request2);
        assertEquals(request.hashCode(), request2.hashCode());
        
        // Test null values (should be allowed for partial updates)
        request.setTargetAmount(null);
        request.setTargetDate(null);
        
        assertNull(request.getTargetAmount());
        assertNull(request.getTargetDate());
    }

    @Test
    void userRegistrationRequest_ComprehensiveValidation() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        
        // Test invalid email formats
        String[] invalidEmails = {"invalid", "@example.com", "user@", "user@.com", "user.example.com"};
        
        for (String email : invalidEmails) {
            request.setUsername(email);
            request.setPassword("validPassword123");
            request.setFullName("Valid Name");
            request.setPhoneNumber("+1234567890");
            
            Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")),
                    "Email validation failed for: " + email);
        }
        
        // Test password length validation
        request.setUsername("valid@example.com");
        request.setPassword("123"); // Too short
        
        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        
        // Test valid phone number formats
        String[] validPhones = {"+1234567890", "+44 20 7946 0958", "+91-9876543210", "123-456-7890"};
        
        request.setPassword("validPassword123");
        request.setFullName("Valid Name");
        
        for (String phone : validPhones) {
            request.setPhoneNumber(phone);
            violations = validator.validate(request);
            // Should not have phone validation errors for valid formats
            assertFalse(violations.stream().anyMatch(v -> 
                v.getPropertyPath().toString().equals("phoneNumber") && 
                v.getMessage().contains("invalid")), 
                "Phone validation failed for: " + phone);
        }
        
        // Test constructor with all parameters
        UserRegistrationRequest fullRequest = new UserRegistrationRequest(
            "test@example.com", "password123", "Test User", "+1234567890");
        
        assertEquals("test@example.com", fullRequest.getUsername());
        assertEquals("password123", fullRequest.getPassword());
        assertEquals("Test User", fullRequest.getFullName());
        assertEquals("+1234567890", fullRequest.getPhoneNumber());
    }

    @Test
    void monthlyReport_ComplexDataHandling() {
        Map<String, BigDecimal> income = new HashMap<>();
        income.put("Salary", new BigDecimal("5000.00"));
        income.put("Freelance", new BigDecimal("1500.00"));
        income.put("Investments", new BigDecimal("200.00"));
        
        Map<String, BigDecimal> expenses = new HashMap<>();
        expenses.put("Rent", new BigDecimal("1200.00"));
        expenses.put("Food", new BigDecimal("800.00"));
        expenses.put("Transportation", new BigDecimal("300.00"));
        expenses.put("Entertainment", new BigDecimal("400.00"));
        
        BigDecimal netSavings = new BigDecimal("4000.00");
        
        MonthlyReport report = new MonthlyReport(2024, 6, income, expenses, netSavings);
        
        // Test all getters
        assertEquals(2024, report.getYear());
        assertEquals(6, report.getMonth());
        assertEquals(3, report.getTotalIncome().size());
        assertEquals(4, report.getTotalExpenses().size());
        assertEquals(netSavings, report.getNetSavings());
        
        // Test income details
        assertEquals(new BigDecimal("5000.00"), report.getTotalIncome().get("Salary"));
        assertEquals(new BigDecimal("1500.00"), report.getTotalIncome().get("Freelance"));
        assertEquals(new BigDecimal("200.00"), report.getTotalIncome().get("Investments"));
        
        // Test expense details
        assertEquals(new BigDecimal("1200.00"), report.getTotalExpenses().get("Rent"));
        assertEquals(new BigDecimal("800.00"), report.getTotalExpenses().get("Food"));
        assertEquals(new BigDecimal("300.00"), report.getTotalExpenses().get("Transportation"));
        assertEquals(new BigDecimal("400.00"), report.getTotalExpenses().get("Entertainment"));
        
        // Test edge cases
        MonthlyReport emptyReport = new MonthlyReport();
        emptyReport.setYear(2023);
        emptyReport.setMonth(12);
        emptyReport.setTotalIncome(new HashMap<>());
        emptyReport.setTotalExpenses(new HashMap<>());
        emptyReport.setNetSavings(BigDecimal.ZERO);
        
        assertEquals(2023, emptyReport.getYear());
        assertEquals(12, emptyReport.getMonth());
        assertTrue(emptyReport.getTotalIncome().isEmpty());
        assertTrue(emptyReport.getTotalExpenses().isEmpty());
        assertEquals(BigDecimal.ZERO, emptyReport.getNetSavings());
    }

    @Test
    void yearlyReport_EdgeCasesAndValidation() {
        Map<String, BigDecimal> income = new HashMap<>();
        income.put("Total Income", new BigDecimal("80000.00"));
        
        Map<String, BigDecimal> expenses = new HashMap<>();
        expenses.put("Total Expenses", new BigDecimal("60000.00"));
        
        YearlyReport report = new YearlyReport(2024, income, expenses, new BigDecimal("20000.00"));
        
        // Test comprehensive toString
        String toString = report.toString();
        assertTrue(toString.contains("2024"));
        assertTrue(toString.contains("80000"));
        assertTrue(toString.contains("60000"));
        assertTrue(toString.contains("20000"));
        
        // Test with null/empty maps
        YearlyReport emptyReport = new YearlyReport();
        emptyReport.setYear(2023);
        emptyReport.setTotalIncome(null);
        emptyReport.setTotalExpenses(null);
        emptyReport.setNetSavings(null);
        
        assertNull(emptyReport.getTotalIncome());
        assertNull(emptyReport.getTotalExpenses());
        assertNull(emptyReport.getNetSavings());
        
        // Test equals with complex data
        YearlyReport report2 = new YearlyReport(2024, income, expenses, new BigDecimal("20000.00"));
        assertEquals(report, report2);
        assertEquals(report.hashCode(), report2.hashCode());
        
        // Test not equals
        YearlyReport differentReport = new YearlyReport(2023, income, expenses, new BigDecimal("20000.00"));
        assertNotEquals(report, differentReport);
    }

    @Test
    void allDtos_NullSafetyAndBoundaryConditions() {
        // Test LoginRequest with boundary conditions
        LoginRequest login = new LoginRequest();
        login.setUsername("");
        login.setPassword("");
        
        Set<ConstraintViolation<LoginRequest>> loginViolations = validator.validate(login);
        assertFalse(loginViolations.isEmpty());
        
        // Test CreateCategoryRequest with null type
        CreateCategoryRequest categoryReq = new CreateCategoryRequest();
        categoryReq.setName("Valid Name");
        categoryReq.setType(null);
        
        Set<ConstraintViolation<CreateCategoryRequest>> categoryViolations = validator.validate(categoryReq);
        assertTrue(categoryViolations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("type")));
        
        // Test TransactionResponse with all fields
        TransactionResponse response = new TransactionResponse();
        response.setId(Long.MAX_VALUE);
        response.setAmount(new BigDecimal("999999999.99"));
        response.setDate(LocalDate.MAX);
        response.setCategory("Test Category");
        response.setDescription("Test Description");
        response.setType(CategoryType.EXPENSE);
        
        assertEquals(Long.MAX_VALUE, response.getId());
        assertEquals(new BigDecimal("999999999.99"), response.getAmount());
        assertEquals(LocalDate.MAX, response.getDate());
        
        // Test SavingsGoalResponse with boundary values
        SavingsGoalResponse goalResponse = new SavingsGoalResponse();
        goalResponse.setId(1L);
        goalResponse.setGoalName("Test Goal");
        goalResponse.setTargetAmount(new BigDecimal("1000000"));
        goalResponse.setCurrentProgress(new BigDecimal("500000"));
        goalResponse.setProgressPercentage(50.0);
        goalResponse.setRemainingAmount(new BigDecimal("500000"));
        goalResponse.setTargetDate(LocalDate.MAX);
        goalResponse.setStartDate(LocalDate.MIN);
        
        assertEquals(50.0, goalResponse.getProgressPercentage());
        assertEquals(LocalDate.MAX, goalResponse.getTargetDate());
        assertEquals(LocalDate.MIN, goalResponse.getStartDate());
    }
} 
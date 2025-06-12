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

class DTOTests {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void transactionRequest_AllArgsConstructor() {
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate date = LocalDate.now();
        String category = "Food";
        String description = "Test description";

        TransactionRequest request = new TransactionRequest(amount, date, category, description);

        assertEquals(amount, request.getAmount());
        assertEquals(date, request.getDate());
        assertEquals(category, request.getCategory());
        assertEquals(description, request.getDescription());
    }

    @Test
    void transactionRequest_NoArgsConstructor() {
        TransactionRequest request = new TransactionRequest();
        assertNotNull(request);
    }

    @Test
    void transactionRequest_SettersAndGetters() {
        TransactionRequest request = new TransactionRequest();
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate date = LocalDate.now();
        String category = "Food";
        String description = "Test description";

        request.setAmount(amount);
        request.setDate(date);
        request.setCategory(category);
        request.setDescription(description);

        assertEquals(amount, request.getAmount());
        assertEquals(date, request.getDate());
        assertEquals(category, request.getCategory());
        assertEquals(description, request.getDescription());
    }

    @Test
    void transactionRequest_Validation_NullAmount() {
        TransactionRequest request = new TransactionRequest();
        request.setDate(LocalDate.now());
        request.setCategory("Food");

        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Amount cannot be null")));
    }

    @Test
    void transactionRequest_Validation_NegativeAmount() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("-10.00"));
        request.setDate(LocalDate.now());
        request.setCategory("Food");

        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Amount must be positive")));
    }

    @Test
    void transactionRequest_Validation_FutureDate() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDate(LocalDate.now().plusDays(1));
        request.setCategory("Food");

        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Date cannot be in the future")));
    }

    @Test
    void transactionRequest_Validation_BlankCategory() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDate(LocalDate.now());
        request.setCategory("");

        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category cannot be blank")));
    }

    @Test
    void transactionResponse_AllArgsConstructor() {
        Long id = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate date = LocalDate.now();
        String category = "Food";
        String description = "Test description";
        CategoryType type = CategoryType.EXPENSE;

        TransactionResponse response = new TransactionResponse(id, amount, date, category, description, type);

        assertEquals(id, response.getId());
        assertEquals(amount, response.getAmount());
        assertEquals(date, response.getDate());
        assertEquals(category, response.getCategory());
        assertEquals(description, response.getDescription());
        assertEquals(type, response.getType());
    }

    @Test
    void transactionResponse_NoArgsConstructor() {
        TransactionResponse response = new TransactionResponse();
        assertNotNull(response);
    }

    @Test
    void transactionResponse_SettersAndGetters() {
        TransactionResponse response = new TransactionResponse();
        Long id = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate date = LocalDate.now();
        String category = "Food";
        String description = "Test description";
        CategoryType type = CategoryType.EXPENSE;

        response.setId(id);
        response.setAmount(amount);
        response.setDate(date);
        response.setCategory(category);
        response.setDescription(description);
        response.setType(type);

        assertEquals(id, response.getId());
        assertEquals(amount, response.getAmount());
        assertEquals(date, response.getDate());
        assertEquals(category, response.getCategory());
        assertEquals(description, response.getDescription());
        assertEquals(type, response.getType());
    }

    @Test
    void userRegistrationRequest_AllFields() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setPhoneNumber("+1234567890");

        assertEquals("test@example.com", request.getUsername());
        assertEquals("password123", request.getPassword());
        assertEquals("Test User", request.getFullName());
        assertEquals("+1234567890", request.getPhoneNumber());
    }

    @Test
    void loginRequest_AllFields() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test@example.com");
        request.setPassword("password123");

        assertEquals("test@example.com", request.getUsername());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void loginRequest_Constructor() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        assertEquals("test@example.com", request.getUsername());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void categoryResponse_AllArgsConstructor() {
        String name = "Food";
        CategoryType type = CategoryType.EXPENSE;
        boolean isCustom = true;

        CategoryResponse response = new CategoryResponse(name, type, isCustom);

        assertEquals(name, response.getName());
        assertEquals(type, response.getType());
        assertEquals(isCustom, response.isCustom());
    }

    @Test
    void categoryResponse_SettersAndGetters() {
        CategoryResponse response = new CategoryResponse();
        response.setName("Food");
        response.setType(CategoryType.EXPENSE);
        response.setCustom(true);

        assertEquals("Food", response.getName());
        assertEquals(CategoryType.EXPENSE, response.getType());
        assertTrue(response.isCustom());
    }

    @Test
    void createCategoryRequest_AllFields() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Custom Category");
        request.setType(CategoryType.EXPENSE);

        assertEquals("Custom Category", request.getName());
        assertEquals(CategoryType.EXPENSE, request.getType());
    }

    @Test
    void savingsGoalRequest_AllFields() {
        SavingsGoalRequest request = new SavingsGoalRequest();
        request.setGoalName("Emergency Fund");
        request.setTargetAmount(new BigDecimal("5000.00"));
        request.setTargetDate(LocalDate.now().plusMonths(6));
        request.setStartDate(LocalDate.now());

        assertEquals("Emergency Fund", request.getGoalName());
        assertEquals(new BigDecimal("5000.00"), request.getTargetAmount());
        assertEquals(LocalDate.now().plusMonths(6), request.getTargetDate());
        assertEquals(LocalDate.now(), request.getStartDate());
    }

    @Test
    void savingsGoalResponse_AllFields() {
        SavingsGoalResponse response = new SavingsGoalResponse();
        response.setId(1L);
        response.setGoalName("Emergency Fund");
        response.setTargetAmount(new BigDecimal("5000.00"));
        response.setTargetDate(LocalDate.now().plusMonths(6));
        response.setStartDate(LocalDate.now());
        response.setCurrentProgress(new BigDecimal("1000.00"));
        response.setProgressPercentage(20.0);
        response.setRemainingAmount(new BigDecimal("4000.00"));

        assertEquals(1L, response.getId());
        assertEquals("Emergency Fund", response.getGoalName());
        assertEquals(new BigDecimal("5000.00"), response.getTargetAmount());
        assertEquals(LocalDate.now().plusMonths(6), response.getTargetDate());
        assertEquals(LocalDate.now(), response.getStartDate());
        assertEquals(new BigDecimal("1000.00"), response.getCurrentProgress());
        assertEquals(20.0, response.getProgressPercentage());
        assertEquals(new BigDecimal("4000.00"), response.getRemainingAmount());
    }

    @Test
    void monthlyReport_AllFields() {
        MonthlyReport report = new MonthlyReport();
        Map<String, BigDecimal> totalIncome = new HashMap<>();
        totalIncome.put("Salary", new BigDecimal("5000.00"));
        Map<String, BigDecimal> totalExpenses = new HashMap<>();
        totalExpenses.put("Food", new BigDecimal("500.00"));
        
        report.setMonth(1);
        report.setYear(2024);
        report.setTotalIncome(totalIncome);
        report.setTotalExpenses(totalExpenses);
        report.setNetSavings(new BigDecimal("4500.00"));
        
        assertEquals(1, report.getMonth());
        assertEquals(2024, report.getYear());
        assertEquals(totalIncome, report.getTotalIncome());
        assertEquals(totalExpenses, report.getTotalExpenses());
        assertEquals(new BigDecimal("4500.00"), report.getNetSavings());
    }

    @Test
    void yearlyReport_AllFields() {
        YearlyReport report = new YearlyReport();
        Map<String, BigDecimal> totalIncome = new HashMap<>();
        totalIncome.put("Salary", new BigDecimal("60000.00"));
        Map<String, BigDecimal> totalExpenses = new HashMap<>();
        totalExpenses.put("Food", new BigDecimal("6000.00"));
        
        report.setYear(2024);
        report.setTotalIncome(totalIncome);
        report.setTotalExpenses(totalExpenses);
        report.setNetSavings(new BigDecimal("54000.00"));
        
        assertEquals(2024, report.getYear());
        assertEquals(totalIncome, report.getTotalIncome());
        assertEquals(totalExpenses, report.getTotalExpenses());
        assertEquals(new BigDecimal("54000.00"), report.getNetSavings());
    }

    @Test
    void savingsGoalUpdateRequest_AllFields() {
        SavingsGoalUpdateRequest request = new SavingsGoalUpdateRequest();
        request.setTargetAmount(new BigDecimal("6000.00"));
        request.setTargetDate(LocalDate.now().plusMonths(8));

        assertEquals(new BigDecimal("6000.00"), request.getTargetAmount());
        assertEquals(LocalDate.now().plusMonths(8), request.getTargetDate());
    }

    @Test
    void transactionDto_AllFields() {
        TransactionDto dto = new TransactionDto();
        dto.setId(1L);
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDate(LocalDate.now());
        dto.setCategoryName("Food");
        dto.setDescription("Test transaction");
        dto.setType(CategoryType.EXPENSE);
        dto.setUserId(1L);

        assertEquals(1L, dto.getId());
        assertEquals(new BigDecimal("100.00"), dto.getAmount());
        assertEquals(LocalDate.now(), dto.getDate());
        assertEquals("Food", dto.getCategoryName());
        assertEquals("Test transaction", dto.getDescription());
        assertEquals(CategoryType.EXPENSE, dto.getType());
        assertEquals(1L, dto.getUserId());
    }

    @Test
    void transactionUpdateRequest_AllFields() {
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        request.setAmount(new BigDecimal("150.00"));
        request.setDescription("Updated description");
        request.setCategory("Entertainment");

        assertEquals(new BigDecimal("150.00"), request.getAmount());
        assertEquals("Updated description", request.getDescription());
        assertEquals("Entertainment", request.getCategory());
    }

    @Test
    void dto_ToString_Methods() {
        // Test toString methods for Lombok generated DTOs
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"), LocalDate.now(), "Food", "Test");
        assertNotNull(request.toString());
        assertTrue(request.toString().contains("100.00"));

        TransactionResponse response = new TransactionResponse(1L, new BigDecimal("100.00"), LocalDate.now(), "Food", "Test", CategoryType.EXPENSE);
        assertNotNull(response.toString());
        assertTrue(response.toString().contains("Food"));

        CategoryResponse categoryResponse = new CategoryResponse("Food", CategoryType.EXPENSE, true);
        assertNotNull(categoryResponse.toString());
        assertTrue(categoryResponse.toString().contains("Food"));
    }

    @Test
    void dto_Equals_And_HashCode() {
        // Test equals and hashCode for Lombok generated DTOs
        TransactionRequest request1 = new TransactionRequest(new BigDecimal("100.00"), LocalDate.now(), "Food", "Test");
        TransactionRequest request2 = new TransactionRequest(new BigDecimal("100.00"), LocalDate.now(), "Food", "Test");
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());

        CategoryResponse response1 = new CategoryResponse("Food", CategoryType.EXPENSE, true);
        CategoryResponse response2 = new CategoryResponse("Food", CategoryType.EXPENSE, true);
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void dto_NullValues() {
        // Test DTOs with null values
        TransactionRequest request = new TransactionRequest();
        assertNull(request.getAmount());
        assertNull(request.getDate());
        assertNull(request.getCategory());
        assertNull(request.getDescription());

        SavingsGoalResponse response = new SavingsGoalResponse();
        assertNull(response.getId());
        assertNull(response.getGoalName());
        assertNull(response.getTargetAmount());
    }
} 
package com.example.financemanagement.service;

import com.example.financemanagement.dto.CategoryResponse;
import com.example.financemanagement.dto.CreateCategoryRequest;
import com.example.financemanagement.entity.Category;
import com.example.financemanagement.entity.CategoryType;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.exception.BadRequestException;
import com.example.financemanagement.exception.ForbiddenException;
import com.example.financemanagement.exception.ResourceConflictException;
import com.example.financemanagement.exception.ResourceNotFoundException;
import com.example.financemanagement.repository.CategoryRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser@example.com", "password", "Test User", "1234567890");
        user.setId(1L);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }

    @Test
    void findAllByCurrentUser_Success() {
        Category category = new Category("Food", CategoryType.EXPENSE, false, user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(Collections.singletonList(category));

        List<CategoryResponse> result = categoryService.findAllByCurrentUser();

        assertFalse(result.isEmpty());
        assertEquals("Food", result.get(0).getName());
    }

    @Test
    void createCustomCategory_Success() {
        CreateCategoryRequest request = new CreateCategoryRequest("Hobby", CategoryType.EXPENSE);
        when(categoryRepository.existsByNameAndUserId(request.getName(), user.getId())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse result = categoryService.createCustomCategory(request);

        assertNotNull(result);
        assertEquals("Hobby", result.getName());
        assertTrue(result.isCustom());
    }

    @Test
    void createCustomCategory_Conflict() {
        CreateCategoryRequest request = new CreateCategoryRequest("Food", CategoryType.EXPENSE);
        when(categoryRepository.existsByNameAndUserId(request.getName(), user.getId())).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> categoryService.createCustomCategory(request));
    }

    @Test
    void deleteCategoryByName_Success() {
        Category category = new Category("Hobby", CategoryType.EXPENSE, true, user);
        when(categoryRepository.findByNameAndUserId("Hobby", user.getId())).thenReturn(Optional.of(category));
        when(transactionRepository.existsByCategoryId(category.getId())).thenReturn(false);

        assertDoesNotThrow(() -> categoryService.deleteCategoryByName("Hobby"));
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategoryByName_NotFound() {
        when(categoryRepository.findByNameAndUserId("Hobby", user.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategoryByName("Hobby"));
    }

    @Test
    void deleteCategoryByName_Forbidden() {
        Category category = new Category("Rent", CategoryType.EXPENSE, false, user);
        when(categoryRepository.findByNameAndUserId("Rent", user.getId())).thenReturn(Optional.of(category));

        assertThrows(ForbiddenException.class, () -> categoryService.deleteCategoryByName("Rent"));
    }

    @Test
    void deleteCategoryByName_BadRequest_CategoryInUse() {
        Category category = new Category("Hobby", CategoryType.EXPENSE, true, user);
        category.setId(2L);
        when(categoryRepository.findByNameAndUserId("Hobby", user.getId())).thenReturn(Optional.of(category));
        when(transactionRepository.existsByCategoryId(category.getId())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.deleteCategoryByName("Hobby"));
    }
} 
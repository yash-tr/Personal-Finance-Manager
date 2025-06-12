package com.example.financemanagement.service;

import com.example.financemanagement.dto.CategoryResponse;
import com.example.financemanagement.dto.CreateCategoryRequest;
import com.example.financemanagement.entity.Category;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.exception.BadRequestException;
import com.example.financemanagement.exception.ForbiddenException;
import com.example.financemanagement.exception.ResourceConflictException;
import com.example.financemanagement.exception.ResourceNotFoundException;
import com.example.financemanagement.repository.CategoryRepository;
import com.example.financemanagement.repository.TransactionRepository;
import com.example.financemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing financial categories.
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Finds all categories (default and custom) for the currently authenticated user.
     * @return A list of category data transfer objects.
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllByCurrentUser() {
        User user = getCurrentUser();
        return categoryRepository.findByUserId(user.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new custom category for the currently authenticated user.
     * @param request DTO containing the details of the category to create.
     * @return The newly created category as a DTO.
     * @throws ResourceConflictException if a category with the same name already exists for the user.
     */
    @Transactional
    public CategoryResponse createCustomCategory(CreateCategoryRequest request) {
        User user = getCurrentUser();
        if (categoryRepository.existsByNameAndUserId(request.getName(), user.getId())) {
            throw new ResourceConflictException("Category with name '" + request.getName() + "' already exists.");
        }
        Category category = new Category(request.getName(), request.getType(), true, user);
        Category savedCategory = categoryRepository.save(category);
        return convertToResponse(savedCategory);
    }

    /**
     * Deletes a custom category by its name for the currently authenticated user.
     * Default categories cannot be deleted. A category in use by a transaction also cannot be deleted.
     * @param name The name of the custom category to delete.
     * @throws ResourceNotFoundException if the category is not found.
     * @throws ForbiddenException if the user tries to delete a default category.
     * @throws BadRequestException if the category is currently associated with any transactions.
     */
    @Transactional
    public void deleteCustomCategory(String name) {
        User user = getCurrentUser();
        Category category = categoryRepository.findByNameAndUserId(name, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Custom category '" + name + "' not found."));

        if (!category.isCustom()) {
            throw new ForbiddenException("Cannot delete default categories.");
        }

        if (transactionRepository.existsByCategoryId(category.getId())) {
            throw new BadRequestException("Cannot delete category that is in use by a transaction.");
        }

        categoryRepository.delete(category);
    }

    /**
     * Deletes a category by its name for the current user.
     * This method is aligned with the API specification.
     * @param name The name of the category to delete.
     */
    @Transactional
    public void deleteCategoryByName(String name) {
        User user = getCurrentUser();
        Category category = categoryRepository.findByNameAndUserId(name, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category '" + name + "' not found."));

        if (!category.isCustom()) {
            throw new ForbiddenException("Cannot delete default categories.");
        }

        if (transactionRepository.existsByCategoryId(category.getId())) {
            throw new BadRequestException("Cannot delete category that is in use by a transaction.");
        }

        categoryRepository.delete(category);
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

    /**
     * Converts a Category entity to a CategoryResponse DTO.
     * @param category The category entity to convert.
     * @return The corresponding DTO.
     */
    private CategoryResponse convertToResponse(Category category) {
        return new CategoryResponse(category.getName(), category.getType(), category.isCustom());
    }
}

package com.example.financemanagement.controller;

import com.example.financemanagement.dto.CategoryResponse;
import com.example.financemanagement.dto.CreateCategoryRequest;
import com.example.financemanagement.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for managing categories.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Gets all categories for the current user.
     * @return A response entity containing a list of categories.
     */
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<CategoryResponse> categories = categoryService.findAllByCurrentUser();
        return ResponseEntity.ok(Map.of("categories", categories));
    }

    /**
     * Creates a new custom category.
     * @param request The request body containing the category details.
     * @return The created category.
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCustomCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse newCategory = categoryService.createCustomCategory(request);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    /**
     * Deletes a custom category by name.
     * @param name The name of the category to delete.
     * @return A success message.
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteCategory(@PathVariable String name) {
        categoryService.deleteCategoryByName(name);
        return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
    }
} 
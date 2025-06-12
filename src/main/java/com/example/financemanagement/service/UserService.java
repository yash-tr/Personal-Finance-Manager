package com.example.financemanagement.service;

import com.example.financemanagement.dto.UserRegistrationRequest;
import com.example.financemanagement.entity.Category;
import com.example.financemanagement.entity.CategoryType;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.exception.ResourceConflictException;
import com.example.financemanagement.repository.CategoryRepository;
import com.example.financemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Service class for managing user-related operations including registration, authentication,
 * and user details retrieval. This service implements Spring Security's UserDetailsService
 * to provide authentication capabilities.
 * 
 * <p>This service handles:
 * <ul>
 *   <li>User registration with validation and password encryption</li>
 *   <li>User authentication through Spring Security integration</li>
 *   <li>Retrieval of current authenticated user context</li>
 *   <li>User details loading for security framework</li>
 * </ul>
 * 
 * @author Finance Management Team
 * @version 1.0
 * @since 1.0
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserService with the required dependencies.
     * 
     * @param userRepository the repository for user data operations
     * @param categoryRepository the repository for category data operations
     * @param passwordEncoder the encoder for password hashing and verification
     */
    @Autowired
    public UserService(UserRepository userRepository, CategoryRepository categoryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user in the system with the provided registration details.
     * 
     * <p>This method performs the following operations:
     * <ul>
     *   <li>Validates that the username (email) is not already taken</li>
     *   <li>Encrypts the password using BCrypt encoding</li>
     *   <li>Creates and persists the new user entity</li>
     * </ul>
     * 
     * @param request the user registration request containing username, password, full name, and phone number
     * @return the newly created and persisted User entity
     * @throws ResourceConflictException if a user with the given username already exists
     * @throws IllegalArgumentException if the request is null or contains invalid data
     */
    public User registerUser(UserRegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Registration request cannot be null");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException("Username already exists: " + request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());

        User savedUser = userRepository.save(user);

        createDefaultCategoriesForUser(savedUser);

        return savedUser;
    }

    /**
     * Creates a set of default income and expense categories for a new user.
     *
     * @param user The user for whom to create the default categories.
     */
    private void createDefaultCategoriesForUser(User user) {
        List<Category> defaultCategories = Arrays.asList(
            new Category("Salary", CategoryType.INCOME, false, user),
            new Category("Food", CategoryType.EXPENSE, false, user),
            new Category("Rent", CategoryType.EXPENSE, false, user),
            new Category("Transportation", CategoryType.EXPENSE, false, user),
            new Category("Entertainment", CategoryType.EXPENSE, false, user),
            new Category("Healthcare", CategoryType.EXPENSE, false, user),
            new Category("Utilities", CategoryType.EXPENSE, false, user)
        );
        categoryRepository.saveAll(defaultCategories);
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     * 
     * <p>This method extracts the user information from Spring Security's 
     * SecurityContextHolder and returns the corresponding User entity from the database.
     * 
     * @return the currently authenticated User entity
     * @throws UsernameNotFoundException if no user is currently authenticated or 
     *         the authenticated user cannot be found in the database
     * @throws IllegalStateException if the security context is in an invalid state
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found in security context");
        }
        
        String username = authentication.getName();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Loads user details by username for Spring Security authentication.
     * 
     * <p>This method is part of the UserDetailsService interface implementation
     * and is called by Spring Security during the authentication process. It
     * retrieves the user from the database and wraps it in a UserDetails object.
     * 
     * @param username the username (email) of the user to load
     * @return a UserDetails object containing the user's authentication information
     * @throws UsernameNotFoundException if no user with the given username exists
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
} 
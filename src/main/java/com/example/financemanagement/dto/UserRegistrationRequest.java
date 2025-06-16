package com.example.financemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration requests.
 * 
 * <p>This DTO encapsulates all the information required to register a new user
 * in the personal finance management system. It includes validation annotations
 * to ensure data integrity and security requirements are met.
 * 
 * <p>Validation rules:
 * <ul>
 *   <li>Username must be a valid email address format</li>
 *   <li>Password must be at least 8 characters long</li>
 *   <li>Full name cannot be blank</li>
 *   <li>Phone number cannot be blank</li>
 * </ul>
 * 
 * <p>This DTO is used in the registration API endpoint and is validated
 * automatically by Spring's validation framework before processing.
 * 
 * @author Finance Management Team
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    
    /**
     * The username for the new user account, must be a valid email address.
     * This will be used for authentication and must be unique across all users.
     */
    @NotBlank(message = "Username is required")
    @Email(message = "Username must be a valid email address")
    private String username;

    /**
     * The password for the new user account.
     * Must be at least 8 characters long for security requirements.
     * Will be encrypted before storage using BCrypt.
     */
    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    /**
     * The full display name of the user.
     * Cannot be blank and will be displayed in the user interface.
     */
    @NotBlank(message = "Full name is required")
    private String fullName;

    /**
     * The contact phone number for the user.
     * Cannot be blank and should include country code for international numbers.
     */
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
} 
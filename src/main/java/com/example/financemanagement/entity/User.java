package com.example.financemanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a user in the personal finance management system.
 * 
 * <p>This entity serves as the central user record that maintains user account information
 * and establishes relationships with all user-specific data including transactions,
 * categories, and savings goals. Each user's data is completely isolated from other users
 * to ensure privacy and security.
 * 
 * <p>The entity includes:
 * <ul>
 *   <li>Unique identification and authentication credentials</li>
 *   <li>Personal information (full name, phone number)</li>
 *   <li>One-to-many relationships with transactions, categories, and savings goals</li>
 *   <li>Cascade operations for automatic cleanup of related data</li>
 * </ul>
 * 
 * <p>Database constraints:
 * <ul>
 *   <li>Username must be unique across all users</li>
 *   <li>All personal information fields are required</li>
 *   <li>Related entities are deleted when user is deleted (cascade)</li>
 * </ul>
 * 
 * @author Finance Management Team
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Unique identifier for the user entity.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username for the user, typically an email address.
     * Used for authentication and must be unique across all users.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Encrypted password for user authentication.
     * Stored as a BCrypt hash for security.
     */
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    /**
     * Full display name of the user.
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * Phone number of the user for contact purposes.
     */
    @Column(nullable = false)
    private String phoneNumber;

    /**
     * Collection of all transactions belonging to this user.
     * Configured with cascade delete and lazy loading for performance.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();

    /**
     * Collection of all categories (both default and custom) belonging to this user.
     * Configured with cascade delete and lazy loading for performance.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Category> categories = new ArrayList<>();

    /**
     * Collection of all savings goals belonging to this user.
     * Configured with cascade delete and lazy loading for performance.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SavingsGoal> savingsGoals = new ArrayList<>();

    /**
     * Default constructor for JPA and framework use.
     * Initializes empty collections for related entities.
     */
    public User() {
    }

    /**
     * Constructor for creating a new user with all required fields.
     * 
     * @param username the unique username (email) for the user
     * @param password the encrypted password for authentication
     * @param fullName the full display name of the user
     * @param phoneNumber the contact phone number for the user
     */
    public User(String username, String password, String fullName, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<SavingsGoal> getSavingsGoals() {
        return savingsGoals;
    }

    public void setSavingsGoals(List<SavingsGoal> savingsGoals) {
        this.savingsGoals = savingsGoals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
} 
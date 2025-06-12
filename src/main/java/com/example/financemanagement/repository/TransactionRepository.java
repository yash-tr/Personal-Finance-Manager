package com.example.financemanagement.repository;

import com.example.financemanagement.entity.CategoryType;
import com.example.financemanagement.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);
    
    boolean existsByCategoryId(Long categoryId);

    List<Transaction> findByUserIdAndDateBetweenAndCategoryIdOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate, Long categoryId);
    
    List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<Transaction> findByUserIdAndCategoryIdOrderByDateDesc(Long userId, Long categoryId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal calculateTotalAmountByTypeAndDateRange(@Param("userId") Long userId, @Param("type") CategoryType type, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:startDate IS NULL OR t.date >= :startDate) " +
           "AND (:endDate IS NULL OR t.date <= :endDate) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId)" +
           "ORDER BY t.date DESC")
    List<Transaction> findTransactionsByFilters(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
            "AND t.date >= :startDate AND t.date <= :endDate")
    List<Transaction> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
} 
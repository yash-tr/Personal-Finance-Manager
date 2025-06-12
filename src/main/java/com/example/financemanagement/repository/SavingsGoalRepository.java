package com.example.financemanagement.repository;

import com.example.financemanagement.entity.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUserId(Long userId);
    Optional<SavingsGoal> findByIdAndUserId(Long id, Long userId);
} 
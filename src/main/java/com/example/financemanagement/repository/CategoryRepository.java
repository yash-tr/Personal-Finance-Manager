package com.example.financemanagement.repository;

import com.example.financemanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId);
    Optional<Category> findByNameAndUserId(String name, Long userId);
    boolean existsByNameAndUserId(String name, Long userId);
    boolean existsByIdAndIsCustom(Long id, boolean isCustom);
    Optional<Category> findByIdAndUserId(Long id, Long userId);
} 
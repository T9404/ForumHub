package com.example.core.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    List<CategoryEntity> findByNameContainingIgnoreCase(String name);

    List<CategoryEntity> findByPreviousCategoryId(UUID previousCategoryId);

    List<CategoryEntity> findByPreviousCategoryIdIsNull();
}

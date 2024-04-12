package com.example.category.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    List<CategoryEntity> findByNameContainingIgnoreCase(String name);

    List<CategoryEntity> findByPreviousCategoryId(UUID previousCategoryId);

    List<CategoryEntity> findByPreviousCategoryIdIsNull();

    @Query(value = """
            WITH RECURSIVE category_tree AS (
                SELECT category_id, previous_category_id FROM category
                WHERE category_id IN (:category_id)
                UNION ALL
                SELECT c.category_id, c.previous_category_id FROM category_tree ct
                JOIN category c ON c.previous_category_id = ct.category_id
            )
            SELECT category_id FROM category_tree;
            """, nativeQuery = true)
    List<UUID> findAllChildCategories(@Param("category_id") UUID categoryId);
}

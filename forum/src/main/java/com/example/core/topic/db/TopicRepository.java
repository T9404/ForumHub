package com.example.core.topic.db;

import com.example.core.category.db.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<TopicEntity, UUID> {
    List<TopicEntity> findByNameContainingIgnoreCase(String name);

    List<TopicEntity> findAllByCategory(CategoryEntity category);
}

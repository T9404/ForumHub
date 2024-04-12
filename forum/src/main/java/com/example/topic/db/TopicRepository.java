package com.example.topic.db;

import com.example.category.db.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<TopicEntity, UUID> {
    List<TopicEntity> findByNameContainingIgnoreCase(String name);

    List<TopicEntity> findAllByCategory(CategoryEntity category);
}

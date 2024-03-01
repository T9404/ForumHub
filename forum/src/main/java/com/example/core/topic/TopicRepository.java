package com.example.core.topic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TopicRepository extends JpaRepository<TopicEntity, UUID> {
}

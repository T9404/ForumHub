package com.example.core.assignment.repository;

import com.example.core.assignment.repository.entity.AssignmentEntity;
import com.example.core.assignment.repository.entity.AssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<AssignmentEntity, AssignmentId> {
    boolean existsByIdUserId(UUID userId);

    Set<AssignmentEntity> findAllByIdUserId(UUID userId);
}

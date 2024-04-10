package com.example.core.assignment;

import com.example.core.assignment.repository.AssignmentRepository;
import com.example.core.assignment.repository.entity.AssignmentEntity;
import com.example.core.assignment.repository.entity.AssignmentId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;

    public boolean hasNoneAssignment(String userId) {
        return !assignmentRepository.existsByIdUserId(UUID.fromString(userId));
    }

    public void assignCategory(String userId, String categoryId) {
        var assignment = new AssignmentEntity();

        var id = AssignmentId.builder()
                .categoryId(UUID.fromString(categoryId))
                .userId(UUID.fromString(userId))
                .build();

        assignment.setId(id);
        assignmentRepository.save(assignment);
    }

    public void removeAssignment(String userId, String categoryId) {
        var id = AssignmentId.builder()
                .categoryId(UUID.fromString(categoryId))
                .userId(UUID.fromString(userId))
                .build();

        assignmentRepository.deleteById(id);
    }

    public Set<UUID> findAssignedCategories(UUID userId) {
        return assignmentRepository.findAllByIdUserId(userId).stream()
                .map(assignmentEntity -> assignmentEntity.getId().getCategoryId())
                .collect(Collectors.toSet());
    }
}

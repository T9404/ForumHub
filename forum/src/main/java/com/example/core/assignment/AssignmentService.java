package com.example.core.assignment;

import com.example.core.assignment.client.AssignmentClient;
import com.example.core.category.db.CategoryEntity;
import com.example.core.category.db.CategoryRepository;
import com.example.security.dto.user.User;
import com.example.security.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static com.example.core.util.RoleUtil.hasRole;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentClient assignmentClient;
    private final CategoryRepository categoryRepository;

    public boolean hasModeratorPermission(CategoryEntity category, User user) {
        return category.getCreatorId().equals(user.getUserId()) || hasAuthorities(category.getCategoryId(), user.getUserId())
                || hasRole(user, RoleType.ADMIN);
    }

    private boolean hasAuthorities(UUID categoryId, UUID userId) {
        var assignmentsDto = assignmentClient.getModeratorAssignments(userId);
        var categoriesId = assignmentsDto.categories();

        if (categoriesId.contains(categoryId)) {
            return true;
        }

        return hasPermissionRecursively(categoriesId, categoryId);
    }

    /**
     * Check if getting category is a child of moderator's category
     */
    private boolean hasPermissionRecursively(Set<UUID> categoriesId, UUID categoryId) {
        for (UUID possibleCategory : categoriesId) {
            var childCategories = categoryRepository.findAllChildCategories(possibleCategory);
            if (childCategories.contains(categoryId)) {
                return true;
            }
        }
        return false;
    }
}

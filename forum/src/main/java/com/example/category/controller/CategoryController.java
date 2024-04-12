package com.example.category.controller;

import com.example.category.db.CategoryService;
import com.example.common.PageResponse;
import com.example.category.controller.request.CreateCategoryRequestDto;
import com.example.category.controller.request.GetCategoryByNameRequest;
import com.example.category.controller.request.GetCategoryRequest;
import com.example.category.controller.request.UpdateCategoryRequestDto;
import com.example.category.controller.response.CategoryHierarchyDto;
import com.example.category.controller.response.CategoryResponseDto;
import com.example.category.controller.response.CreateCategoryResponseDto;
import com.example.security.dto.user.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public CreateCategoryResponseDto createCategory(@RequestBody CreateCategoryRequestDto request,
                                                    @AuthenticationPrincipal User user
    ) {
        return categoryService.create(request, user);
    }

    @PatchMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public CategoryResponseDto updateCategory(@RequestBody UpdateCategoryRequestDto request,
                                              @AuthenticationPrincipal User user
    ) {
        return categoryService.update(request, user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @DeleteMapping("/{category_id}")
    public void deleteCategory(@PathVariable("category_id") UUID categoryId,
                               @AuthenticationPrincipal User user
    ) {
        categoryService.delete(categoryId, user);
    }

    @GetMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public CategoryResponseDto getCategory(@PathVariable UUID categoryId) {
        return categoryService.findById(categoryId);
    }

    @GetMapping("/hierarchy")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public List<CategoryHierarchyDto> getAllCategoriesWithHierarchy(@RequestBody GetCategoryRequest request) {
        return categoryService.findAllWithHierarchy(request);
    }

    @GetMapping("/all/{page}/{size}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public PageResponse<CategoryResponseDto> getAllCategories(@PathVariable int page, @PathVariable int size) {
        return categoryService.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/getting-by-name")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public List<CategoryResponseDto> getCategoryByName(@RequestBody GetCategoryByNameRequest request) {
        return categoryService.findByName(request);
    }
}

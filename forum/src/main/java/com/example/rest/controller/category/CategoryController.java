package com.example.rest.controller.category;

import com.example.core.category.db.CategoryService;
import com.example.public_interface.category.*;
import com.example.public_interface.page.PageResponse;
import com.example.rest.controller.category.dto.CreateCategoryRequestDto;
import com.example.rest.controller.category.dto.GetCategoryRequest;
import com.example.rest.controller.category.dto.UpdateCategoryRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/creating")
    public CreateCategoryResponseDto createCategory(@RequestBody CreateCategoryRequestDto request) {
        return categoryService.save(request);
    }

    @PatchMapping("/updating")
    public CategoryResponseDto updateCategory(@RequestBody UpdateCategoryRequestDto request) {
        return categoryService.update(request);
    }

    @DeleteMapping("/deleting/{categoryId}")
    public void deleteCategory(@PathVariable("categoryId") UUID categoryId) {
        categoryService.delete(categoryId);
    }

    @GetMapping("/{categoryId}")
    public CategoryResponseDto getCategory(@PathVariable UUID categoryId) {
        return categoryService.findById(categoryId);
    }

    @GetMapping("/hierarchy")
    public List<CategoryHierarchyDto> getAllCategories(@RequestBody GetCategoryRequest request) {
        return categoryService.getAllWithHierarchy(request);
    }

    @GetMapping("/all/{page}/{size}")
    public PageResponse<CategoryResponseDto> getAllCategories(@PathVariable int page, @PathVariable int size) {
        return categoryService.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/getting-by-name")
    public List<CategoryResponseDto> getCategoryByName(@RequestBody GetCategoryByNameDto request) {
        return categoryService.findByName(request);
    }
}

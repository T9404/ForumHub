package com.example.core.category;

import com.example.public_interface.category.CreateCategoryRequestDto;
import com.example.public_interface.category.UpdateCategoryRequestDto;
import com.example.public_interface.category.CreateCategoryResponseDto;
import com.example.public_interface.category.CategoryResponseDto;
import com.example.rest.configuration.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CreateCategoryResponseDto save(CreateCategoryRequestDto request) {
        var category = CategoryEntity.builder()
                .previousCategoryId(request.previousCategoryId())
                .name(request.name())
                .createdAt(OffsetDateTime.now())
                .modificationAt(OffsetDateTime.now())
                .creatorId(request.creatorId())
                .build();

        var savedCategory = categoryRepository.save(category);
        return new CreateCategoryResponseDto(savedCategory.getCategoryId());
    }

    public CategoryResponseDto update(UpdateCategoryRequestDto request) {
        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException(CategoryEvent.CATEGORY_NOT_FOUND, "Category with id " + request.categoryId() + " not found"));

        category.setPreviousCategoryId(request.previousCategoryId());
        category.setName(request.name());
        category.setModificationAt(OffsetDateTime.now());

        var updatedCategory = categoryRepository.save(category);
        return CategoryResponseDto.builder()
                .categoryId(updatedCategory.getCategoryId())
                .previousCategoryId(updatedCategory.getPreviousCategoryId())
                .name(updatedCategory.getName())
                .createdAt(updatedCategory.getCreatedAt())
                .modificationAt(updatedCategory.getModificationAt())
                .creatorId(updatedCategory.getCreatorId())
                .build();
    }

    public void delete(UUID categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public CategoryResponseDto findById(UUID categoryId) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(CategoryEvent.CATEGORY_NOT_FOUND, "Category with id " + categoryId + " not found"));

        return CategoryResponseDto.builder()
                .categoryId(category.getCategoryId())
                .previousCategoryId(category.getPreviousCategoryId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .modificationAt(category.getModificationAt())
                .creatorId(category.getCreatorId())
                .build();
    }
}

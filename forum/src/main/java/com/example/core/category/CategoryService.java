package com.example.core.category;

import com.example.public_interface.category.*;
import com.example.rest.configuration.BusinessException;
import com.example.public_interface.page.PageResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        log.info("Category with id {} has been created", savedCategory.getCategoryId());

        return new CreateCategoryResponseDto(savedCategory.getCategoryId());
    }

    public CategoryResponseDto update(UpdateCategoryRequestDto request) {
        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException(CategoryEvent.CATEGORY_NOT_FOUND, "Category with id " + request.categoryId() + " not found"));

        category.setPreviousCategoryId(request.previousCategoryId());
        category.setName(request.name());
        category.setModificationAt(OffsetDateTime.now());

        var updatedCategory = categoryRepository.save(category);
        log.info("Category with id {} has been updated", updatedCategory.getCategoryId());

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
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(CategoryEvent.CATEGORY_NOT_FOUND, "Category with id " + categoryId + " not found"));
        categoryRepository.delete(category);
        log.info("Category with id {} has been deleted", category.getCategoryId());
    }

    public CategoryResponseDto findById(UUID categoryId) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(CategoryEvent.CATEGORY_NOT_FOUND, "Category with id " + categoryId + " not found"));
        log.info("Category with id {} has been found", category.getCategoryId());

        return CategoryResponseDto.builder()
                .categoryId(category.getCategoryId())
                .previousCategoryId(category.getPreviousCategoryId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .modificationAt(category.getModificationAt())
                .creatorId(category.getCreatorId())
                .build();
    }

    public PageResponse<CategoryResponseDto> findAll(PageRequest pageRequest) {
        var categories = categoryRepository.findAll(pageRequest);
        log.info(categories.getTotalElements() + " categories have been found");

        PageResponse.Metadata metadata = new PageResponse.Metadata(categories.getNumber(), categories.getSize(), categories.getTotalElements());

        var categoriesDto = categories.map(CategoryMapper.INSTANCE::toDto).getContent();

        return new PageResponse<>(categoriesDto, metadata);
    }

    public List<CategoryResponseDto> findByName(GetCategoryByNameDto request) {
        var categories = categoryRepository.findByNameContainingIgnoreCase(request.name());
        log.info(categories.size() + " categories have been found");

        return categories.stream().map(CategoryMapper.INSTANCE::toDto).toList();
    }

    public List<CategoryHierarchyDto> getAllWithHierarchy() {
        return categoryRepository.findByPreviousCategoryIdIsNull().stream()
                .map(this::buildCategoryHierarchy)
                .collect(Collectors.toList());
    }

    private CategoryHierarchyDto buildCategoryHierarchy(CategoryEntity category) {
        var children = categoryRepository.findByPreviousCategoryId(category.getCategoryId()).stream()
                .map(this::buildCategoryHierarchy)
                .sorted(Comparator.comparing(CategoryHierarchyDto::createdAt))
                .collect(Collectors.toList());

        return new CategoryHierarchyDto(
                category.getCategoryId(),
                category.getPreviousCategoryId(),
                category.getName(),
                category.getCreatedAt(),
                category.getModificationAt(),
                category.getCreatorId(),
                children
        );
    }
}

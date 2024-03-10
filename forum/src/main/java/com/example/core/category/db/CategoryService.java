package com.example.core.category.db;

import com.example.core.category.mapper.CategoryMapper;
import com.example.core.category.enums.CategoryEvent;
import com.example.core.common.enums.OrderSortingType;
import com.example.core.topic.db.TopicService;
import com.example.public_interface.category.*;
import com.example.core.common.exception.BusinessException;
import com.example.public_interface.page.PageResponse;
import com.example.rest.controller.category.dto.CreateCategoryRequestDto;
import com.example.rest.controller.category.dto.GetCategoryRequest;
import com.example.rest.controller.category.dto.UpdateCategoryRequestDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final TopicService topicService;

    public CreateCategoryResponseDto save(CreateCategoryRequestDto request) {
        checkExistsTopics(request);

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

    public List<CategoryEntity> findChildren(UUID previousCategoryId) {
        return categoryRepository.findByPreviousCategoryId(previousCategoryId);
    }

    public List<CategoryHierarchyDto> getAllWithHierarchy(GetCategoryRequest request) {
        GetCategoryRequest finalRequest = getFinalRequest(request);
        Comparator<CategoryEntity> comparator = getComparator(finalRequest);

        return categoryRepository.findByPreviousCategoryIdIsNull()
                .stream()
                .sorted(comparator)
                .map(category -> buildCategoryHierarchy(category, finalRequest))
                .collect(Collectors.toList());
    }

    private void checkExistsTopics(CreateCategoryRequestDto request) {
        var previousCategory = categoryRepository.findById(request.previousCategoryId())
                .orElseThrow(() -> new BusinessException(CategoryEvent.CATEGORY_NOT_FOUND, "Category with id " + request.previousCategoryId() + " not found"));

        var topicPreviousCategories = topicService.findTopicsByCategory(previousCategory);

        if (!topicPreviousCategories.isEmpty()) {
            throw new BusinessException(CategoryEvent.CATEGORY_HAS_TOPICS, "Category with id " + request.previousCategoryId() + " has topics");
        }
    }

    private GetCategoryRequest getFinalRequest(GetCategoryRequest request) {
        return new GetCategoryRequest(
                Optional.ofNullable(request.topicSorting()).orElse("name"),
                Optional.ofNullable(request.topicDirection()).orElse(OrderSortingType.ASC.name())
        );
    }

    private CategoryHierarchyDto buildCategoryHierarchy(CategoryEntity category, GetCategoryRequest request) {
        Comparator<CategoryEntity> comparator = getComparator(request);

        var children = categoryRepository.findByPreviousCategoryId(category.getCategoryId()).stream()
                .sorted(comparator)
                .map(child -> buildCategoryHierarchy(child, request))
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

    private Comparator<CategoryEntity> getComparator(GetCategoryRequest request) {
        Comparator<CategoryEntity> comparator;

        switch (request.topicSorting().toString()) {
            case "name":
                comparator = Comparator.comparing(CategoryEntity::getName);
                break;
            case "created_at":
                comparator = Comparator.comparing(CategoryEntity::getCreatedAt);
                break;
            case "modification_at":
                comparator = Comparator.comparing(CategoryEntity::getModificationAt);
                break;
            default:
                comparator = Comparator.comparing(CategoryEntity::getName);
        }

        if ("desc".equalsIgnoreCase(request.topicDirection())) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}

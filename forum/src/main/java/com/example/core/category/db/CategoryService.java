package com.example.core.category.db;

import com.example.core.assignment.AssignmentService;
import com.example.core.category.mapper.CategoryMapper;
import com.example.core.category.enums.CategoryEvent;
import com.example.core.common.OrderSortingType;
import com.example.core.topic.db.TopicService;
import com.example.exception.event.UserEvent;
import com.example.exception.BusinessException;
import com.example.public_interface.page.PageResponse;
import com.example.rest.category.request.CreateCategoryRequestDto;
import com.example.rest.category.request.GetCategoryByNameRequest;
import com.example.rest.category.request.GetCategoryRequest;
import com.example.rest.category.request.UpdateCategoryRequestDto;
import com.example.rest.category.response.CategoryHierarchyDto;
import com.example.rest.category.response.CategoryResponseDto;
import com.example.rest.category.response.CreateCategoryResponseDto;
import com.example.security.dto.user.User;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.public_interface.page.PageResponse.Metadata.createMetadata;

@Slf4j
@Service
public class CategoryService {
    private static final String DENIED_MESSAGE = "You do not have permission to perform this action";
    private final CategoryRepository categoryRepository;
    private final AssignmentService assignmentService;
    private final TopicService topicService;

    public CategoryService(CategoryRepository categoryRepository, @Lazy TopicService topicService,
                           AssignmentService assignmentService) {
        this.categoryRepository = categoryRepository;
        this.topicService = topicService;
        this.assignmentService = assignmentService;
    }

    public CreateCategoryResponseDto create(CreateCategoryRequestDto request, User user) {
        checkPreviousCategoryId(request.previousCategoryId());

        var category = buildCategory(request, user);

        var savedCategory = categoryRepository.save(category);
        return new CreateCategoryResponseDto(savedCategory.getCategoryId());
    }

    public CategoryResponseDto update(UpdateCategoryRequestDto request, User user) {
        var category = findCategoryById(request.categoryId());

        if (!assignmentService.hasModeratorPermission(category, user)) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, DENIED_MESSAGE);
        }

        if (request.previousCategoryId() != null) {
            checkPreviousCategoryId(request.previousCategoryId());
            category.setPreviousCategoryId(request.previousCategoryId());
        }

        if (StringUtils.isNotBlank(request.name())) {
            category.setName(request.name());
        }

        category.setModificationAt(OffsetDateTime.now());
        var updatedCategory = categoryRepository.save(category);
        return toDto(updatedCategory);
    }

    public void delete(UUID categoryId, User user) {
        var category = findCategoryById(categoryId);

        if (!assignmentService.hasModeratorPermission(category, user)) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, DENIED_MESSAGE);
        }

        var topics = topicService.findTopicsByCategory(category);
        if (!topics.isEmpty()) {
            throw new BusinessException(CategoryEvent.CATEGORY_HAS_TOPICS, "Category with id " + categoryId + " has topics");
        }

        categoryRepository.delete(category);
    }

    public CategoryResponseDto findById(UUID categoryId) {
        var category = findCategoryById(categoryId);
        return toDto(category);
    }

    public PageResponse<CategoryResponseDto> findAll(PageRequest pageRequest) {
        var categories = categoryRepository.findAll(pageRequest);

        var categoriesDto = categories.getContent().stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .toList();

        PageResponse.Metadata metadata = createMetadata(pageRequest, categories.getTotalElements());
        return new PageResponse<>(categoriesDto, metadata);
    }

    public List<CategoryResponseDto> findByName(GetCategoryByNameRequest request) {
        var categories = categoryRepository.findByNameContainingIgnoreCase(request.name());
        return categories.stream().map(CategoryMapper.INSTANCE::toDto).toList();
    }

    public List<CategoryEntity> findChildren(UUID previousCategoryId) {
        return categoryRepository.findByPreviousCategoryId(previousCategoryId);
    }

    public List<CategoryHierarchyDto> findAllWithHierarchy(GetCategoryRequest request) {
        GetCategoryRequest finalRequest = getFinalRequest(request);
        Comparator<CategoryEntity> comparator = getComparator(finalRequest);

        return categoryRepository.findByPreviousCategoryIdIsNull()
                .stream()
                .sorted(comparator)
                .map(category -> buildCategoryHierarchy(category, finalRequest))
                .toList();
    }

    private CategoryEntity buildCategory(CreateCategoryRequestDto request, User user) {
        return CategoryEntity.builder()
                .previousCategoryId(request.previousCategoryId())
                .name(request.name())
                .createdAt(OffsetDateTime.now())
                .modificationAt(OffsetDateTime.now())
                .creatorId(user.getUserId())
                .build();
    }

    private CategoryEntity findCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(CategoryEvent.CATEGORY_NOT_FOUND, "Category with id " + categoryId + " not found"));
    }

    private void checkPreviousCategoryId(UUID previousCategoryId) {
        if (previousCategoryId == null) {
            return;
        }
        var previousCategory = findCategoryById(previousCategoryId);

        var topicPreviousCategories = topicService.findTopicsByCategory(previousCategory);
        if (!topicPreviousCategories.isEmpty()) {
            throw new BusinessException(CategoryEvent.CATEGORY_HAS_TOPICS, "Category with id " + previousCategoryId + " has topics");
        }
    }

    private GetCategoryRequest getFinalRequest(GetCategoryRequest request) {
        return new GetCategoryRequest(
                Optional.ofNullable(request.topicSorting()).orElse("name"),
                Optional.ofNullable(request.topicDirection()).orElse(OrderSortingType.ASC.name())
        );
    }

    private CategoryResponseDto toDto(CategoryEntity updatedCategory) {
        return CategoryResponseDto.builder()
                .categoryId(updatedCategory.getCategoryId())
                .previousCategoryId(updatedCategory.getPreviousCategoryId())
                .name(updatedCategory.getName())
                .createdAt(updatedCategory.getCreatedAt())
                .modificationAt(updatedCategory.getModificationAt())
                .creatorId(updatedCategory.getCreatorId())
                .build();
    }

    private CategoryHierarchyDto buildCategoryHierarchy(CategoryEntity category, GetCategoryRequest request) {
        Comparator<CategoryEntity> comparator = getComparator(request);

        var children = categoryRepository.findByPreviousCategoryId(category.getCategoryId()).stream()
                .sorted(comparator)
                .map(child -> buildCategoryHierarchy(child, request))
                .toList();

        return toDto(category, children);
    }

    private Comparator<CategoryEntity> getComparator(GetCategoryRequest request) {
        Comparator<CategoryEntity> comparator = switch (request.topicSorting().toLowerCase()) {
            case "created_at" -> Comparator.comparing(CategoryEntity::getCreatedAt);
            case "modification_at" -> Comparator.comparing(CategoryEntity::getModificationAt);
            default -> Comparator.comparing(CategoryEntity::getName);
        };

        if ("desc".equalsIgnoreCase(request.topicDirection())) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    private CategoryHierarchyDto toDto(CategoryEntity category, List<CategoryHierarchyDto> children) {
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

package com.example.core.topic.db;

import com.example.core.assignment.AssignmentService;
import com.example.core.category.db.CategoryEntity;
import com.example.core.category.db.CategoryService;
import com.example.core.category.enums.CategoryEvent;
import com.example.core.common.OrderSortingType;
import com.example.core.common.PageEvent;
import com.example.core.topic.enums.TopicEvent;
import com.example.core.topic.enums.TopicType;
import com.example.core.topic.mapper.TopicMapper;
import com.example.core.topic.enums.TopicSorting;
import com.example.exception.event.UserEvent;
import com.example.exception.BusinessException;
import com.example.rest.category.response.CategoryResponseDto;
import com.example.core.category.mapper.CategoryMapper;
import com.example.public_interface.page.PageResponse;
import com.example.rest.topic.request.GetAllTopicsRequestDto;
import com.example.rest.topic.request.TopicRequestDto;
import com.example.rest.topic.request.UpdateTopicRequestDto;
import com.example.rest.topic.response.CreateTopicResponseDto;
import com.example.rest.topic.response.GetTopicByNameDto;
import com.example.rest.topic.response.TopicResponseDto;
import com.example.security.dto.user.User;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class TopicService {
    private static final String ACCESS_DENIED_MESSAGE = "Not enough permission";
    private final AssignmentService assignmentService;
    private final TopicRepository topicRepository;
    private final CategoryService categoryService;

    public TopicService(TopicRepository topicRepository, @Lazy CategoryService categoryService,
                        AssignmentService assignmentService) {
        this.topicRepository = topicRepository;
        this.categoryService = categoryService;
        this.assignmentService = assignmentService;
    }

    public CreateTopicResponseDto save(TopicRequestDto request, User user) {
        var category = getCategory(request.categoryId());
        checkLastPositionHierarchy(category);

        var topic = buildTopicEntity(request, user, category);
        var savedTopic = topicRepository.save(topic);

        log.info("Topic with id {} has been created", savedTopic.getTopicId());
        return new CreateTopicResponseDto(savedTopic.getTopicId());
    }

    public List<TopicEntity> findTopicsByCategory(CategoryEntity category) {
        return topicRepository.findAllByCategory(category);
    }

    public TopicEntity findById(UUID topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new BusinessException(TopicEvent.TOPIC_NOT_FOUND, "Topic with id " + topicId + " not found"));
    }

    public void archiveTopic(UUID topicId, User user) {
        var topic = findById(topicId);

        if (!assignmentService.hasModeratorPermission(topic.getCategory(), user)) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, ACCESS_DENIED_MESSAGE);
        }

        topic.setStatus(TopicType.ARCHIVED.name());
        topicRepository.save(topic);
    }

    public void unarchiveTopic(UUID topicId, User user) {
        var topic = findById(topicId);

        if (!assignmentService.hasModeratorPermission(topic.getCategory(), user)) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, ACCESS_DENIED_MESSAGE);
        }

        topic.setStatus(TopicType.ACTIVE.name());
        topicRepository.save(topic);
    }

    public void delete(UUID topicId, User user) {
        var topic = findById(topicId);

        if (!assignmentService.hasModeratorPermission(topic.getCategory(), user)
                && (!topic.getCreatorId().equals(user.getUserId()))) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, ACCESS_DENIED_MESSAGE);
        }

        topicRepository.delete(topic);
    }

    public TopicResponseDto update(UpdateTopicRequestDto request, User user) {
        var topic = findById(request.topicId());

        if (!assignmentService.hasModeratorPermission(topic.getCategory(), user)
                && (!topic.getCreatorId().equals(user.getUserId()))) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, ACCESS_DENIED_MESSAGE);
        }

        if (request.categoryId() != null) {
            var category = categoryService.findById(request.categoryId());
            topic.setCategory(CategoryMapper.INSTANCE.toEntity(category));
        }

        if (StringUtils.isNotBlank(request.name())) {
            topic.setName(request.name());
        }

        topic.setModificationAt(OffsetDateTime.now());
        var updatedTopic = topicRepository.save(topic);

        return TopicMapper.INSTANCE.toResponse(updatedTopic);
    }

    public PageResponse<TopicResponseDto> findAll(GetAllTopicsRequestDto request) {
        var finalRequest = validateRequest(request);

        var pageRequest = PageRequest.of(finalRequest.page(), finalRequest.size(),
                Objects.equals(finalRequest.orderSortingType(), OrderSortingType.DESC.getValue()) ?
                        Sort.Direction.DESC : Sort.Direction.ASC, finalRequest.topicSorting());

        var topics = topicRepository.findAll(pageRequest);

        PageResponse.Metadata metadata = new PageResponse.Metadata(topics.getNumber(), topics.getSize(), topics.getTotalElements());

        return new PageResponse<>(topics.map(TopicMapper.INSTANCE::toResponse).getContent(), metadata);
    }

    public List<TopicResponseDto> findByName(GetTopicByNameDto request) {
        var topics = topicRepository.findByNameContainingIgnoreCase(request.name());
        return topics.stream().map(TopicMapper.INSTANCE::toResponse).toList();
    }

    private TopicEntity buildTopicEntity(TopicRequestDto request, User user, CategoryResponseDto category) {
        return TopicEntity.builder()
                .name(request.name())
                .category(CategoryMapper.INSTANCE.toEntity(category))
                .createdAt(OffsetDateTime.now())
                .modificationAt(OffsetDateTime.now())
                .creatorId(user.getUserId())
                .status(TopicType.ACTIVE.name())
                .build();
    }

    private CategoryResponseDto getCategory(UUID categoryId) {
        if (categoryId == null) {
            throw new BusinessException(CategoryEvent.CATEGORY_INVALID_ID, "Category id cannot be null");
        }

        return categoryService.findById(categoryId);
    }

    private void checkLastPositionHierarchy(CategoryResponseDto category) {
        var childrenCategories = categoryService.findChildren(category.categoryId());

        if (!childrenCategories.isEmpty()) {
            throw new BusinessException(CategoryEvent.CATEGORY_IS_NOT_LEAF, "Category with id " + category.categoryId() + " has children");
        }
    }

    private GetAllTopicsRequestDto validateRequest(GetAllTopicsRequestDto request) {
        if (request.page() < 0) {
            throw new BusinessException(PageEvent.INVALID_PAGE_NUMBER, "Page number cannot be less than 0");
        }

        if (request.size() < 1) {
            throw new BusinessException(PageEvent.INVALID_PAGE_SIZE, "Page size cannot be less than 1");
        }

        return getAllTopicRequestBuild(request);
    }

    private static GetAllTopicsRequestDto getAllTopicRequestBuild(GetAllTopicsRequestDto request) {
        return GetAllTopicsRequestDto.builder()
                .orderSortingType(OrderSortingType.fromValue(request.orderSortingType()).getValue())
                .topicSorting(TopicSorting.fromValue(request.topicSorting()).getValue())
                .page(request.page())
                .size(request.size())
                .build();
    }
}

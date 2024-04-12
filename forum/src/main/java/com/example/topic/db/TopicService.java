package com.example.topic.db;

import com.example.assignment.AssignmentService;
import com.example.category.db.CategoryEntity;
import com.example.category.db.CategoryService;
import com.example.category.enums.CategoryEvent;
import com.example.topic.enums.TopicEvent;
import com.example.topic.enums.TopicType;
import com.example.topic.mapper.TopicMapper;
import com.example.exception.event.UserEvent;
import com.example.exception.BusinessException;
import com.example.category.controller.response.CategoryResponseDto;
import com.example.category.mapper.CategoryMapper;
import com.example.topic.controller.request.TopicRequestDto;
import com.example.topic.controller.request.UpdateTopicRequestDto;
import com.example.topic.controller.response.CreateTopicResponseDto;
import com.example.topic.controller.response.GetTopicByNameDto;
import com.example.topic.controller.response.TopicResponseDto;
import com.example.security.dto.user.User;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
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

    public TopicEntity findById(@Nonnull UUID topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new BusinessException(TopicEvent.TOPIC_NOT_FOUND, "Topic with id " + topicId + " not found"));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void archiveTopic(@Nonnull UUID topicId, User user) {
        var topic = findById(topicId);

        if (!assignmentService.hasModeratorPermission(topic.getCategory(), user)) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, ACCESS_DENIED_MESSAGE);
        }

        topic.setStatus(TopicType.ARCHIVED.name());
        topicRepository.save(topic);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void unarchiveTopic(@Nonnull UUID topicId, User user) {
        var topic = findById(topicId);

        if (!assignmentService.hasModeratorPermission(topic.getCategory(), user)) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, ACCESS_DENIED_MESSAGE);
        }

        topic.setStatus(TopicType.ACTIVE.name());
        topicRepository.save(topic);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void delete(UUID topicId, User user) {
        var topic = findById(topicId);

        if (!assignmentService.hasModeratorPermission(topic.getCategory(), user)
                && (!topic.getCreatorId().equals(user.getUserId()))) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, ACCESS_DENIED_MESSAGE);
        }

        topicRepository.delete(topic);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
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

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<TopicResponseDto> findAll(Pageable pageable) {
        var topics = topicRepository.findAll(pageable);
        return convertPage(topics);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
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

    private CategoryResponseDto getCategory(@Nonnull UUID categoryId) {
        return categoryService.findById(categoryId);
    }

    private void checkLastPositionHierarchy(CategoryResponseDto category) {
        var childrenCategories = categoryService.findChildren(category.categoryId());

        if (!childrenCategories.isEmpty()) {
            throw new BusinessException(CategoryEvent.CATEGORY_IS_NOT_LEAF, "Category with id " + category.categoryId() + " has children");
        }
    }

    private Page<TopicResponseDto> convertPage(Page<TopicEntity> topicEntityPage) {
        var topicResponses = topicEntityPage.getContent().stream()
                .map(TopicMapper.INSTANCE::toResponse)
                .toList();
        return new PageImpl<>(topicResponses, topicEntityPage.getPageable(), topicEntityPage.getTotalElements());
    }
}

package com.example.core.topic.db;

import com.example.core.category.db.CategoryEntity;
import com.example.core.category.db.CategoryService;
import com.example.core.category.enums.CategoryEvent;
import com.example.core.common.enums.OrderSortingType;
import com.example.core.common.enums.PageEvent;
import com.example.core.topic.enums.TopicEvent;
import com.example.core.topic.mapper.TopicMapper;
import com.example.core.topic.enums.TopicSorting;
import com.example.public_interface.category.CategoryResponseDto;
import com.example.public_interface.topic.*;
import com.example.core.common.exception.BusinessException;
import com.example.core.category.mapper.CategoryMapper;
import com.example.public_interface.page.PageResponse;
import com.example.rest.controller.topic.dto.GetAllTopicsRequestDto;
import com.example.rest.controller.topic.dto.TopicRequestDto;
import com.example.rest.controller.topic.dto.UpdateTopicRequestDto;
import lombok.AllArgsConstructor;
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
    private final TopicRepository topicRepository;
    private final CategoryService categoryService;

    public TopicService(TopicRepository topicRepository, @Lazy CategoryService categoryService) {
        this.topicRepository = topicRepository;
        this.categoryService = categoryService;
    }

    public CreateTopicResponseDto save(TopicRequestDto request) {
        var category = getCategory(request.categoryId());

        checkLastPositionHierarchy(category);

        var topic = TopicEntity.builder()
                .name(request.name())
                .category(CategoryMapper.INSTANCE.toEntity(category))
                .createdAt(OffsetDateTime.now())
                .modificationAt(OffsetDateTime.now())
                .creatorId(request.authorId())
                .build();

        var savedTopic = topicRepository.save(topic);
        log.info("Topic with id {} has been created", savedTopic.getTopicId());

        return new CreateTopicResponseDto(savedTopic.getTopicId());
    }

    public List<TopicEntity> findTopicsByCategory(CategoryEntity category) {
        return topicRepository.findAllByCategory(category);
    }

    public void delete(UUID topicId) {
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new BusinessException(TopicEvent.TOPIC_NOT_FOUND, "Topic with id " + topicId + " not found"));

        topicRepository.delete(topic);
        log.info("Topic with id {} has been deleted", topic.getTopicId());
    }

    public TopicResponseDto findById(UUID topicId) {
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new BusinessException(TopicEvent.TOPIC_NOT_FOUND, "Topic with id " + topicId + " not found"));

        log.info("Topic with id {} has been found", topic.getTopicId());
        return TopicMapper.INSTANCE.toResponse(topic);
    }

    public TopicResponseDto update(UpdateTopicRequestDto request) {
        var topic = topicRepository.findById(request.topicId())
                .orElseThrow(() -> new BusinessException(TopicEvent.TOPIC_NOT_FOUND, "Topic with id " + request.topicId() + " not found"));

        if (request.categoryId() != null) {
            var category = categoryService.findById(request.categoryId());
            topic.setCategory(CategoryMapper.INSTANCE.toEntity(category));
        }

        if (request.name() != null) {
            topic.setName(request.name());
        }

        topic.setModificationAt(OffsetDateTime.now());
        var updatedTopic = topicRepository.save(topic);
        log.info("Topic with id {} has been updated", updatedTopic.getTopicId());

        return TopicMapper.INSTANCE.toResponse(updatedTopic);
    }

    public PageResponse<TopicResponseDto> findAll(GetAllTopicsRequestDto request) {
        var finalRequest = validateRequest(request);

        var pageRequest = PageRequest.of(finalRequest.page(), finalRequest.size(), Objects.equals(finalRequest.orderSortingType(), OrderSortingType.DESC.getValue()) ?
                Sort.Direction.DESC : Sort.Direction.ASC, finalRequest.topicSorting());

        var topics = topicRepository.findAll(pageRequest);
        log.info(topics.getSize() + " topics have been found");

        PageResponse.Metadata metadata = new PageResponse.Metadata(topics.getNumber(), topics.getSize(), topics.getTotalElements());

        return new PageResponse<>(topics.map(TopicMapper.INSTANCE::toResponse).getContent(), metadata);
    }

    public List<TopicResponseDto> findByName(GetTopicByNameDto request) {
        var topics = topicRepository.findByNameContainingIgnoreCase(request.name());
        log.info(topics.size() + " topics have been found");

        return topics.stream().map(TopicMapper.INSTANCE::toResponse).toList();
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

        return GetAllTopicsRequestDto.builder()
                .orderSortingType(OrderSortingType.fromValue(request.orderSortingType().toString()).getValue())
                .topicSorting(TopicSorting.fromValue(request.topicSorting().toString()).getValue())
                .page(request.page())
                .size(request.size())
                .build();
    }
}

package com.example.core.topic;

import com.example.core.category.db.CategoryService;
import com.example.core.common.enums.OrderSortingType;
import com.example.core.common.enums.PageEvent;
import com.example.public_interface.topic.*;
import com.example.core.common.BusinessException;
import com.example.core.category.mapper.CategoryMapper;
import com.example.public_interface.page.PageResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final CategoryService categoryService;

    public CreateTopicResponseDto save(TopicRequestDto request) {
        var category = categoryService.findById(request.categoryId());

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
        validateRequest(request);

        var pageRequest = PageRequest.of(request.page(), request.size(), Objects.equals(request.orderSortingType(), OrderSortingType.DESC.getValue()) ?
                Sort.Direction.DESC : Sort.Direction.ASC, request.topicSorting());

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

    private void validateRequest(GetAllTopicsRequestDto request) {
        if (request.page() < 0) {
            throw new BusinessException(PageEvent.INVALID_PAGE_NUMBER, "Page number cannot be less than 0");
        }

        if (request.size() < 1) {
            throw new BusinessException(PageEvent.INVALID_PAGE_SIZE, "Page size cannot be less than 1");
        }

        OrderSortingType.fromValue(request.orderSortingType());
        TopicSorting.fromValue(request.topicSorting());
    }
}

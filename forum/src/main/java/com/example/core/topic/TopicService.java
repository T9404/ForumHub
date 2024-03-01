package com.example.core.topic;

import com.example.core.category.CategoryService;
import com.example.public_interface.topic.TopicRequestDto;
import com.example.public_interface.topic.UpdateTopicRequestDto;
import com.example.public_interface.topic.CreateTopicResponseDto;
import com.example.public_interface.topic.TopicResponseDto;
import com.example.rest.configuration.BusinessException;
import com.example.public_interface.category.CategoryMapper;
import com.example.public_interface.topic.TopicMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

//TODO: Implement the Role system
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
        return new CreateTopicResponseDto(savedTopic.getTopicId());
    }

    public void delete(UUID topicId) {
        topicRepository.deleteById(topicId);
    }

    public TopicResponseDto findById(UUID topicId) {
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new BusinessException(TopicEvent.TOPIC_NOT_FOUND, "Topic with id " + topicId + " not found"));
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
        return TopicMapper.INSTANCE.toResponse(updatedTopic);
    }
}

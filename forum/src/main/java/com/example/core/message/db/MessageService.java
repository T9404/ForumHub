package com.example.core.message.db;

import com.example.core.message.enums.MessageEvent;
import com.example.core.message.dto.MessageFilter;
import com.example.core.message.mapper.MessageMapper;
import com.example.core.message.enums.MessageSorting;
import com.example.core.topic.db.TopicService;
import com.example.core.common.enums.OrderSortingType;
import com.example.core.common.enums.PageEvent;
import com.example.public_interface.message.*;
import com.example.rest.controller.topic.dto.GetMessageByTopicRequest;
import com.example.core.topic.mapper.TopicMapper;
import com.example.core.common.exception.BusinessException;
import com.example.public_interface.page.PageResponse;
import com.example.rest.controller.message.dto.CreateMessageRequestDto;
import com.example.rest.controller.message.dto.UpdateMessageRequestDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final TopicService topicService;

    public CreateMessageResponseDto create(CreateMessageRequestDto requestDto) {
        var topic = topicService.findById(requestDto.topicId());

        var message = MessageEntity.builder()
                .topic(TopicMapper.INSTANCE.toEntity(topic))
                .content(requestDto.content())
                .createdAt(OffsetDateTime.now())
                .modificationAt(OffsetDateTime.now())
                .creatorId(requestDto.creatorId())
                .build();

        var savedMessage = messageRepository.save(message);

        return new CreateMessageResponseDto(savedMessage.getMessageId());
    }

    public void delete(UUID messageId) {
        var message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(MessageEvent.MESSAGE_NOT_FOUND, "Message with id " + messageId + " not found"));

        messageRepository.delete(message);
        log.info("Message with id {} has been deleted", message.getMessageId());
    }

    public MessageResponseDto update(UpdateMessageRequestDto request) {
        var message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new BusinessException(MessageEvent.MESSAGE_NOT_FOUND, "Message with id " + request.messageId() + " not found"));

        if (request.content() != null) {
            message.setContent(request.content());
        }

        if (request.topicId() != null) {
            var topic = topicService.findById(request.topicId());
            message.setTopic(TopicMapper.INSTANCE.toEntity(topic));
        }

        message.setModificationAt(OffsetDateTime.now());
        messageRepository.save(message);
        log.info("Message with id {} has been updated", message.getMessageId());

        return MessageMapper.INSTANCE.toResponse(message);
    }

    public List<MessageResponseDto> findByContent(GetMessageByContentDto request) {
        var messages = messageRepository.findByContentContainingIgnoreCase(request.content());
        log.info("Messages with content {} have been found", request.content());

        return messages.stream().map(MessageMapper.INSTANCE::toResponse).toList();
    }

    public PageResponse<MessageResponseDto> findAll(MessageFilter filter, PageRequest pageRequest) {
        var messages = messageRepository.findAll(filter, pageRequest);
        log.info(messages.size() + " messages have been found");

        var messageResponseDtos = messages.stream()
                .map(MessageMapper.INSTANCE::toResponse)
                .toList();

        PageResponse.Metadata metadata =
                new PageResponse.Metadata(pageRequest.getPageNumber(), pageRequest.getPageSize(), messages.size());

        return new PageResponse<>(messageResponseDtos, metadata);
    }

    public PageResponse<MessageResponseDto> findAllByTopic(GetMessageByTopicRequest request, int page, int size) {
        var validatedRequest = validateRequest(request);
        checkPageAndSize(page, size);

        var filter = createMessageFilter(validatedRequest);

        var pageRequest = PageRequest.of(page, size,
                validatedRequest.direction().equals(OrderSortingType.DESC.getValue()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                validatedRequest.sortBy());

        return findAll(filter, pageRequest);
    }

    private GetMessageByTopicRequest validateRequest(GetMessageByTopicRequest request) {
        if (request.topicId() == null) {
            throw new BusinessException(MessageEvent.TOPIC_ID_IS_REQUIRED, "Topic id is required");
        }

        return GetMessageByTopicRequest.builder()
                .direction(request.direction() != null && !request.direction().isEmpty() ?
                        OrderSortingType.fromValue(request.direction()).getValue() : OrderSortingType.DESC.getValue())
                .sortBy(request.sortBy() != null && !request.sortBy().isEmpty() ?
                        MessageSorting.fromValue(request.sortBy()).getValue() : MessageSorting.CREATED_AT.getValue())
                .topicId(request.topicId())
                .build();
    }

    private void checkPageAndSize(int page, int size) {
        if (page < 0) {
            throw new BusinessException(PageEvent.INVALID_PAGE_NUMBER, "Page number must be greater than or equal to 0");
        }

        if (size < 0) {
            throw new BusinessException(PageEvent.INVALID_PAGE_SIZE, "Page size must be greater than or equal to 0");
        }
    }

    private MessageFilter createMessageFilter(GetMessageByTopicRequest request) {
        return MessageFilter.builder()
                .topicId(request.topicId())
                .build();
    }
}

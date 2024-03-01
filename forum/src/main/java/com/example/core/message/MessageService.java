package com.example.core.message;

import com.example.core.topic.TopicService;
import com.example.public_interface.message.*;
import com.example.public_interface.topic.TopicMapper;
import com.example.rest.configuration.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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

    public MessageResponseDto findById(UUID messageId) {
        var message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(MessageEvent.MESSAGE_NOT_FOUND, "Message with id " + messageId + " not found"));
        return MessageMapper.INSTANCE.toResponse(message);
    }

    public void delete(UUID messageId) {
        messageRepository.deleteById(messageId);
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

        var savedMessage = messageRepository.save(message);
        return MessageMapper.INSTANCE.toResponse(savedMessage);
    }
}

package com.example.message.db;

import com.example.assignment.AssignmentService;
import com.example.attachment.db.AttachmentService;
import com.example.message.enums.MessageEvent;
import com.example.message.dto.MessageFilter;
import com.example.message.mapper.MessageMapper;
import com.example.message.enums.MessageSorting;
import com.example.topic.db.TopicEntity;
import com.example.topic.db.TopicService;
import com.example.common.OrderSortingType;
import com.example.common.PageEvent;
import com.example.topic.enums.TopicEvent;
import com.example.topic.enums.TopicType;
import com.example.exception.event.UserEvent;
import com.example.exception.BusinessException;
import com.example.message.controller.response.CreateMessageResponseDto;
import com.example.message.controller.response.GetMessageByContentDto;
import com.example.message.controller.response.MessageResponseDto;
import com.example.topic.controller.request.GetMessageByTopicRequest;
import com.example.common.PageResponse;
import com.example.message.controller.request.CreateMessageRequestDto;
import com.example.message.controller.request.UpdateMessageRequestDto;
import com.example.security.dto.user.User;
import com.example.wishlist.WishlistNotificationService;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.common.PageResponse.Metadata.createMetadata;

@Slf4j
@Service
@AllArgsConstructor
public class MessageService {
    private static final String DENIED_MESSAGE = "You do not have permission to perform this action";
    private final AssignmentService assignmentService;
    private final MessageRepository messageRepository;
    private final AttachmentService attachmentService;
    private final WishlistNotificationService wishlistNotificationService;
    private final TopicService topicService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CreateMessageResponseDto create(CreateMessageRequestDto requestDto, MultipartFile[] attachments) {
        var topic = topicService.findById(requestDto.topicId());
        checkIfTopicArchived(topic);

        var message = buildMessageEntity(requestDto, topic);
        var savedMessage = messageRepository.save(message);

        if (attachments != null) {
            attachmentService.saveAttachments(savedMessage, attachments);
        }

        wishlistNotificationService.notifyAllSubscribers(savedMessage);

        return new CreateMessageResponseDto(savedMessage.getMessageId());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public MessageResponseDto findById(UUID messageId) {
        var message = findMessageById(messageId);
        return createMessageResponse(message);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void delete(UUID messageId, User user) {
        var message = findMessageById(messageId);

        var topic = message.getTopic();
        var category = topic.getCategory();
        if (!assignmentService.hasModeratorPermission(category, user) && (!message.getCreatorId().equals(user.getUserId()))) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, DENIED_MESSAGE);
        }

        attachmentService.deleteAllAttachments(message);
        messageRepository.delete(message);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addAttachment(UUID messageId, MultipartFile[] attachments, User user) {
        var message = findByIdWithTopic(messageId);

        var topic = message.getTopic();
        checkIfTopicArchived(topic);

        if (!user.getUserId().equals(message.getCreatorId())) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, DENIED_MESSAGE);
        }

        attachmentService.saveAttachments(message, attachments);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteAttachment(UUID attachmentId, User user) {
        var attachment = attachmentService.findById(attachmentId);
        var message = attachment.getMessage();

        var topic = message.getTopic();
        checkIfTopicArchived(topic);

        var category = topic.getCategory();
        if (!assignmentService.hasModeratorPermission(category, user) && (!message.getCreatorId().equals(user.getUserId()))) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, DENIED_MESSAGE);
        }

        attachmentService.deleteAttachment(attachmentId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public MessageResponseDto update(UpdateMessageRequestDto request, User user) {
        validateUpdateMessageRequest(request);

        var message = findByIdWithTopic(request.messageId());
        var topic = message.getTopic();
        checkIfTopicArchived(topic);

        if (!message.getCreatorId().equals(user.getUserId())) {
            throw new BusinessException(UserEvent.PERMISSION_DENIED, DENIED_MESSAGE);
        }

        updateMessageContent(request.content(), message);
        updateMessageTopic(request.topicId(), message);
        messageRepository.save(message);
        return createMessageResponse(message);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<MessageResponseDto> findByContent(GetMessageByContentDto request) {
        var messages = messageRepository.findByContentContainingIgnoreCase(request.content());

        List<MessageResponseDto> responseDtoList = new ArrayList<>();

        for (MessageEntity message : messages) {
            var messageResponseDto = MessageMapper.INSTANCE.toResponse(message);
            var attachments = attachmentService.getAttachments(messageResponseDto.messageId());
            messageResponseDto.attachments().addAll(attachments);
            responseDtoList.add(messageResponseDto);
        }

        return responseDtoList;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public PageResponse<MessageResponseDto> findAll(MessageFilter filter, PageRequest pageRequest) {
        var messages = messageRepository.findAll(filter, pageRequest);
        var messageResponseDtos = messages.stream()
                .map(MessageMapper.INSTANCE::toResponse)
                .toList();

        int count = messageRepository.count(filter);

        PageResponse.Metadata metadata = createMetadata(pageRequest, count);
        return new PageResponse<>(messageResponseDtos, metadata);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public PageResponse<MessageResponseDto> findAllByTopic(GetMessageByTopicRequest request, int page, int size) {
        var validatedRequest = validateRequest(request);
        checkPageAndSize(page, size);

        var filter = createMessageFilter(validatedRequest);

        var pageRequest = createPageRequest(validatedRequest, page, size);
        return findAll(filter, pageRequest);
    }

    private void checkIfTopicArchived(TopicEntity topic) {
        if (topic.getStatus().equals(TopicType.ARCHIVED.name())) {
            throw new BusinessException(TopicEvent.ARCHIVE_TOPIC, "Unable to write to archived topics");
        }
    }

    private MessageResponseDto createMessageResponse(MessageEntity message) {
        var attachmentsDto = attachmentService.getAttachments(message.getMessageId());
        return MessageMapper.INSTANCE.toResponse(message, attachmentsDto);
    }

    private void validateUpdateMessageRequest(UpdateMessageRequestDto request) {
        if (request.messageId() == null) {
            throw new BusinessException(MessageEvent.MESSAGE_ID_IS_REQUIRED, "Message id is required");
        }
    }

    private void updateMessageContent(String content, MessageEntity message) {
        if (StringUtils.isNotBlank(content)) {
            message.setContent(content);
            message.setModificationAt(OffsetDateTime.now());
        }
    }

    private void updateMessageTopic(UUID topicId, MessageEntity message) {
        if (topicId != null) {
            var addedTopic = topicService.findById(topicId);
            message.setTopic(addedTopic);
            message.setModificationAt(OffsetDateTime.now());
        }
    }

    private MessageEntity buildMessageEntity(CreateMessageRequestDto requestDto, TopicEntity topic) {
        return MessageEntity.builder()
                .topic(topic)
                .content(requestDto.content())
                .createdAt(OffsetDateTime.now())
                .modificationAt(OffsetDateTime.now())
                .creatorId(requestDto.creatorId())
                .build();
    }

    private MessageEntity findMessageById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(MessageEvent.MESSAGE_NOT_FOUND, "Message not found"));
    }

    private MessageEntity findByIdWithTopic(UUID messageId) {
        return messageRepository.findByIdWithTopicFetch(messageId)
                .orElseThrow(() -> new BusinessException(MessageEvent.MESSAGE_NOT_FOUND, "Message with id " + messageId + " not found"));
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

    private PageRequest createPageRequest(GetMessageByTopicRequest request, int page, int size) {
        Sort.Direction direction = request.direction().equals(OrderSortingType.DESC.getValue()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, direction, request.sortBy());
    }
}

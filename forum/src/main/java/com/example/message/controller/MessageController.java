package com.example.message.controller;

import com.example.message.dto.MessageFilter;
import com.example.message.db.MessageService;
import com.example.message.controller.response.CreateMessageResponseDto;
import com.example.message.controller.response.GetMessageByContentDto;
import com.example.message.controller.response.MessageResponseDto;
import com.example.topic.controller.request.GetMessageByTopicRequest;
import com.example.common.PageResponse;
import com.example.message.controller.request.CreateMessageRequestDto;
import com.example.message.controller.request.UpdateMessageRequestDto;
import com.example.security.dto.user.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/messages")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/getting-by-content")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public List<MessageResponseDto> getMessagesByName(@RequestBody GetMessageByContentDto request) {
        return messageService.findByContent(request);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public CreateMessageResponseDto createMessage(@RequestParam("attachments") MultipartFile[] attachments,
                                                  @RequestParam("content") String content,
                                                  @RequestParam("topic_id") UUID topicId,
                                                  @AuthenticationPrincipal User user
    ) {
        var request = CreateMessageRequestDto.builder()
                .content(content)
                .topicId(topicId)
                .creatorId(user.getUserId())
                .build();

        return messageService.create(request, attachments);
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public MessageResponseDto updateMessage(@RequestBody UpdateMessageRequestDto request,
                                            @AuthenticationPrincipal User user
    ) {
        return messageService.update(request, user);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public MessageResponseDto getMessage(@RequestParam("message_id") UUID messageId) {
        return messageService.findById(messageId);
    }

    @PostMapping("/attachment")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public void addAttachment(@RequestParam("message_id") UUID messageId,
                              @RequestParam("attachments") MultipartFile[] attachments,
                              @AuthenticationPrincipal User user
    ) {
        messageService.addAttachment(messageId, attachments, user);
    }

    @DeleteMapping("/attachment")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public void deleteAttachment(@RequestParam("attachment_id") UUID attachmentId,
                                 @AuthenticationPrincipal User user
    ) {
        messageService.deleteAttachment(attachmentId, user);
    }

    @DeleteMapping("/{messageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public void deleteMessage(@PathVariable("messageId") UUID messageId,
                              @AuthenticationPrincipal User user
    ) {
        messageService.delete(messageId, user);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public PageResponse<MessageResponseDto> getAllMessages(@RequestBody MessageFilter filter,
                                                           @RequestParam(value = "page") int page,
                                                           @RequestParam(value = "size") int size) {
        return messageService.findAll(filter, PageRequest.of(page, size));
    }

    @GetMapping("/getting-by-topic")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public PageResponse<MessageResponseDto> getMessagesByTopic(@RequestBody GetMessageByTopicRequest request,
                                                               @RequestParam(value = "page") int page,
                                                               @RequestParam(value = "size") int size) {
        return messageService.findAllByTopic(request, page, size);
    }
}

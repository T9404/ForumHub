package com.example.rest.controller.message;

import com.example.core.message.dto.MessageFilter;
import com.example.core.message.db.MessageService;
import com.example.public_interface.message.*;
import com.example.rest.controller.topic.dto.GetMessageByTopicRequest;
import com.example.public_interface.page.PageResponse;
import com.example.rest.controller.message.dto.CreateMessageRequestDto;
import com.example.rest.controller.message.dto.UpdateMessageRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/messages")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/getting-by-content")
    public List<MessageResponseDto> getMessagesByName(@RequestBody GetMessageByContentDto request) {
        return messageService.findByContent(request);
    }

    @PostMapping("/creating")
    public CreateMessageResponseDto createMessage(@RequestBody CreateMessageRequestDto request) {
        return messageService.create(request);
    }

    @PatchMapping("/updating")
    public MessageResponseDto updateMessage(@RequestBody UpdateMessageRequestDto request) {
        return messageService.update(request);
    }

    @DeleteMapping("/deleting/{messageId}")
    public void deleteMessage(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
    }

    @GetMapping("/all")
    public PageResponse<MessageResponseDto> getAllMessages(@RequestBody MessageFilter filter,
                                                           @RequestParam(value = "page") int page,
                                                           @RequestParam(value = "size") int size) {
        return messageService.findAll(filter, PageRequest.of(page, size));
    }

    @GetMapping("/getting-by-topic")
    public PageResponse<MessageResponseDto> getMessagesByTopic(@RequestBody GetMessageByTopicRequest request,
                                                               @RequestParam(value = "page") int page,
                                                               @RequestParam(value = "size") int size) {
        return messageService.findAllByTopic(request, page, size);
    }
}

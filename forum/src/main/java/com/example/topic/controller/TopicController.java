package com.example.topic.controller;

import com.example.topic.db.TopicService;
import com.example.topic.controller.request.TopicRequestDto;
import com.example.topic.controller.request.UpdateTopicRequestDto;
import com.example.topic.controller.response.CreateTopicResponseDto;
import com.example.topic.controller.response.GetTopicByNameDto;
import com.example.topic.controller.response.TopicResponseDto;
import com.example.security.dto.user.User;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/topics")
public class TopicController {
    private final TopicService topicService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public CreateTopicResponseDto createTopic(@RequestBody TopicRequestDto request,
                                              @AuthenticationPrincipal User user
    ) {
        return topicService.save(request, user);
    }

    @PatchMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public TopicResponseDto updateTopic(@RequestBody UpdateTopicRequestDto request,
                                        @AuthenticationPrincipal User user
    ) {
        return topicService.update(request, user);
    }

    @DeleteMapping("/{topicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public void deleteTopic(@PathVariable("topicId") UUID topicId,
                            @AuthenticationPrincipal User user
    ) {
        topicService.delete(topicId, user);
    }

    @PatchMapping("/archive/{topic_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void archiveTopic(@PathVariable("topic_id") UUID topicId,
                             @AuthenticationPrincipal User user
    ) {
        topicService.archiveTopic(topicId, user);
    }

    @PatchMapping("/unarchive/{topic_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void unarchiveTopic(@PathVariable("topic_id") UUID topicId,
                               @AuthenticationPrincipal User user
    ) {
        topicService.unarchiveTopic(topicId, user);
    }

    @GetMapping("/getting-by-name")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public List<TopicResponseDto> getTopic(@RequestBody GetTopicByNameDto request) {
        return topicService.findByName(request);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public Page<TopicResponseDto> getAllTopics(@Nonnull final Pageable pageable) {
        return topicService.findAll(pageable);
    }

}

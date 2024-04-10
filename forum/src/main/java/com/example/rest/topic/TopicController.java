package com.example.rest.topic;

import com.example.core.topic.db.TopicService;
import com.example.public_interface.page.PageResponse;
import com.example.rest.topic.request.GetAllTopicsRequestDto;
import com.example.rest.topic.request.TopicRequestDto;
import com.example.rest.topic.request.UpdateTopicRequestDto;
import com.example.rest.topic.response.CreateTopicResponseDto;
import com.example.rest.topic.response.GetTopicByNameDto;
import com.example.rest.topic.response.TopicResponseDto;
import com.example.security.dto.user.User;
import lombok.AllArgsConstructor;
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
    public PageResponse<TopicResponseDto> getAllTopics(@RequestBody GetAllTopicsRequestDto request) {
        return topicService.findAll(request);
    }

}

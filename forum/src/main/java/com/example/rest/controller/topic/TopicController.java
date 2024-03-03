package com.example.rest.controller.topic;

import com.example.core.topic.TopicService;
import com.example.public_interface.topic.*;
import com.example.public_interface.page.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/topics")
public class TopicController {
    private final TopicService topicService;

    @PostMapping("/creating")
    public CreateTopicResponseDto createTopic(@RequestBody TopicRequestDto request) {
        return topicService.save(request);
    }

    @PatchMapping("/updating")
    public TopicResponseDto updateTopic(@RequestBody UpdateTopicRequestDto request) {
        return topicService.update(request);
    }

    @DeleteMapping("/deleting/{topicId}")
    public void deleteTopic(@PathVariable("topicId") UUID topicId) {
        topicService.delete(topicId);
    }

    @GetMapping("/getting-by-name")
    public List<TopicResponseDto> getTopic(@RequestBody GetTopicByNameDto request) {
        return topicService.findByName(request);
    }

    @GetMapping("/all")
    public PageResponse<TopicResponseDto> getAllTopics(@RequestBody GetAllTopicsRequestDto request) {
        return topicService.findAll(request);
    }

}

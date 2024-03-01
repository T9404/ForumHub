package com.example.rest.controller.topic;

import com.example.core.topic.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/topics")
public class TopicController {
    private final TopicService topicService;


}

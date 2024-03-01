package com.example.rest.controller.message;

import com.example.core.message.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/messages")
public class MessageController {
    private final MessageService messageService;


}

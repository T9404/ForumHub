package com.example.controller;

import com.example.security.dto.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test")
@RestController
public class TestController {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String test(@AuthenticationPrincipal User user) {
        return user.getUserId() + " " + user.getRoles();
    }
}

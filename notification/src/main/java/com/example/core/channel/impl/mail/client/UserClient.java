package com.example.core.channel.impl.mail.client;

import com.example.contract.auth.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "authClient")
public interface UserClient {

    @GetMapping("/api/v1/admin/users")
    UserDto getUser(@RequestParam(value = "user_id") UUID userId);
}

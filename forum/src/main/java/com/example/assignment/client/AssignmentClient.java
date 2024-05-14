package com.example.assignment.client;

import com.example.contract.auth.AssignmentsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "assignmentClient")
public interface AssignmentClient {

    @GetMapping("/api/v1/admin/assignments")
    AssignmentsDto getModeratorAssignments(@RequestParam(value = "user_id") UUID userId);
}

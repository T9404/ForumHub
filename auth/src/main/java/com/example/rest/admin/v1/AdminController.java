package com.example.rest.admin.v1;

import com.example.contract.AssignmentsDto;
import com.example.core.admin.AdminService;
import com.example.rest.admin.v1.request.CreateUserDto;
import com.example.rest.admin.v1.response.UserDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users/{user_id}")
    public UserDto getUser(@PathVariable(name = "user_id") String userId) {
        return adminService.getUser(userId);
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody @Valid CreateUserDto request) {
        return adminService.createUser(request);
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PatchMapping("/users")
    public UserDto updateUser(@RequestParam(name = "user_id") String userId,
                              @RequestBody @Valid CreateUserDto request) {
        return adminService.updateUser(userId, request);
    }

    @PostMapping("/users/blocking")
    public void blockUser(@RequestParam(name = "user_id") String userId) {
        adminService.blockUser(userId);
    }

    @DeleteMapping("/users/blocking")
    public void unblockUser(@RequestParam(name = "user_id") String userId) {
        adminService.unblockUser(userId);
    }

    @PostMapping("/assignments")
    public void assignRole(@RequestParam(name = "user_id") String userId,
                           @RequestParam(name = "category_id") String categoryId) {
        adminService.assignCategory(userId, categoryId);
    }

    @DeleteMapping("/assignments")
    public void unassignRole(@RequestParam(name = "user_id") String userId,
                             @RequestParam(name = "category_id") String categoryId) {
        adminService.unassignCategory(userId, categoryId);
    }

    @GetMapping("/assignments")
    public AssignmentsDto getAssignment(@RequestParam(name = "user_id") String userId) {
        return adminService.getAssignment(userId);
    }

}

package com.example.rest.admin.v1;

import com.example.core.admin.AdminService;
import com.example.public_interface.admin.UpdateUserDto;
import com.example.rest.admin.v1.request.CreateUserByAdminRequest;
import com.example.rest.admin.v1.response.UserDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public UserDto getUser(@RequestParam(name = "user_id") String userId) {
        return adminService.getUser(userId);
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody @Valid CreateUserByAdminRequest request) {
        return adminService.createUser(request);
    }

    @PutMapping("/users")
    public UserDto updateUser(@RequestParam(name = "user_id") String userId, @RequestBody @Valid CreateUserByAdminRequest request) {
        var dto = new UpdateUserDto(
                request.username(),
                request.email(),
                request.password(),
                request.fullName(),
                request.phoneNumber(),
                request.roleType()
        );
        return adminService.updateUser(userId, dto);
    }

    @PostMapping("/users/blocking")
    public void blockUser(@RequestParam(name = "user_id") String userId) {
        adminService.blockUser(userId);
    }

    @DeleteMapping("/users/blocking")
    public void unblockUser(@RequestParam(name = "user_id") String userId) {
        adminService.unblockUser(userId);
    }

}

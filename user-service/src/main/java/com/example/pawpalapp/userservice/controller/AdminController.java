package com.example.pawpalapp.userservice.controller;

import com.example.pawpalapp.userservice.dto.PetDto;
import com.example.pawpalapp.userservice.dto.UserResponseDto;
import com.example.pawpalapp.userservice.model.User;
import com.example.pawpalapp.userservice.model.enums.Role;
import com.example.pawpalapp.userservice.model.enums.UserStatus;
import com.example.pawpalapp.userservice.service.AdminService;
import com.example.pawpalapp.userservice.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }


//    @GetMapping("/users")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public List<UserResponseDto> getAllUsers() {
//        return userService.getAll();
//    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponseDto> getUsers(
            @RequestParam(required = false) Role role
    ) {
        if (role != null) {
            return adminService.getByRole(role);
        }
        return userService.getAll();
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @PutMapping("/users/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void approveVet(@PathVariable Long id) {
        userService.updateStatus(id, UserStatus.ACTIVE);
    }

    @PutMapping("/users/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void rejectVet(@PathVariable Long id) {
        userService.updateStatus(id, UserStatus.REJECTED);
    }

    @GetMapping("/users/{id}/pets")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<PetDto> getPetsByUserId(@PathVariable Long id) {
        return adminService.getPetsByUserId(id);
    }

    @GetMapping("/users/{id}/profile")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Object getUserProfile(@PathVariable Long id) {
        return adminService.getFullProfile(id);
    }
}

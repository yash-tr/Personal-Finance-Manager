package com.example.financemanagement.controller;

import com.example.financemanagement.dto.UserProfileResponse;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        User currentUser = userService.getCurrentUser();
        UserProfileResponse userProfileResponse = new UserProfileResponse(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getFullName(),
                currentUser.getPhoneNumber()
        );
        return ResponseEntity.ok(userProfileResponse);
    }
} 
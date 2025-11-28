package com.example.bankcards.service;

import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Role;

public interface UserService {
    User registerUser(RegisterRequest registerRequest);
    User findByUsername(String username);
    User findById(Long userId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User updateUserRole(Long userId, Role newRole);
    User createAdminUser(String username, String email, String password);
    boolean isAdmin(User user);
    void disableUser(Long userId);
    void enableUser(Long userId);
}
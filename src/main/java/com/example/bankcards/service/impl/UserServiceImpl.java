package com.example.bankcards.service.impl;

import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        // Проверяем существование пользователя с таким именем
        if (existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        // Проверяем существование пользователя с таким email
        if (existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        // Создаем нового пользователя
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.ROLE_USER); // Используем enum
        user.setEnabled(true);

        // Сохраняем пользователя в базе данных
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        // Находим пользователя по имени пользователя
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь " + username + " не найден"));
    }

    @Override
    public User findById(Long userId) {
        // Находим пользователя по ID
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public boolean existsByUsername(String username) {
        // Проверяем существует ли пользователь с таким именем
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        // Проверяем существует ли пользователь с таким email
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User updateUserRole(Long userId, Role newRole) {
        // Находим пользователя по ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Обновляем роль пользователя
        user.setRole(newRole);

        // Сохраняем изменения
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User createAdminUser(String username, String email, String password) {
        // Проверяем существование пользователя
        if (existsByUsername(username)) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        if (existsByEmail(email)) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        // Создаем администратора
        User admin = new User();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(Role.ROLE_ADMIN); // Устанавливаем роль администратора
        admin.setEnabled(true);

        // Сохраняем администратора в базе данных
        return userRepository.save(admin);
    }

    @Override
    public boolean isAdmin(User user) {
        // Проверяем является ли пользователь администратором
        return user.getRole() == Role.ROLE_ADMIN;
    }

    @Override
    @Transactional
    public void disableUser(Long userId) {
        // Находим пользователя по ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Отключаем пользователя
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void enableUser(Long userId) {
        // Находим пользователя по ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Включаем пользователя
        user.setEnabled(true);
        userRepository.save(user);
    }
}
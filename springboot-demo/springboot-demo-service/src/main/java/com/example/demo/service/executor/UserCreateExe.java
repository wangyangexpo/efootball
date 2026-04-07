package com.example.demo.service.executor;

import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserCreateRequest;
import com.example.demo.common.Constants;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * User create command executor
 */
@Component
public class UserCreateExe {

    @Autowired
    private UserRepository userRepository;

    public UserDTO execute(UserCreateRequest request) {
        // Check if username already exists
        UserEntity existing = userRepository.selectByUsername(request.getUsername());
        if (existing != null) {
            throw new RuntimeException("User already exists with username: " + request.getUsername());
        }

        // Create entity
        UserEntity entity = new UserEntity();
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setStatus(Constants.STATUS_ACTIVE);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted(0);

        // Save
        userRepository.insert(entity);

        // Convert to DTO
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setStatus(entity.getStatus());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());

        return dto;
    }
}
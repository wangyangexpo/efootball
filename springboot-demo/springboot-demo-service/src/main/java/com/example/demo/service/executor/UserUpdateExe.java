package com.example.demo.service.executor;

import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserUpdateRequest;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * User update command executor
 */
@Component
public class UserUpdateExe {

    @Autowired
    private UserRepository userRepository;

    public UserDTO execute(Long id, UserUpdateRequest request) {
        UserEntity entity = userRepository.selectById(id);
        if (entity == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        // Update fields if provided
        if (request.getUsername() != null) {
            entity.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        entity.setUpdateTime(LocalDateTime.now());

        // Save
        userRepository.updateById(entity);

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
package com.example.demo.service.executor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserQueryRequest;
import com.example.demo.common.PageResult;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User query executor
 */
@Component
public class UserQueryExe {

    @Autowired
    private UserRepository userRepository;

    public UserDTO getById(Long id) {
        UserEntity entity = userRepository.selectById(id);
        if (entity == null) {
            return null;
        }
        return convertToDTO(entity);
    }

    public PageResult<UserDTO> list(UserQueryRequest request) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();

        // Filter conditions
        if (request.getUsername() != null) {
            wrapper.like(UserEntity::getUsername, request.getUsername());
        }
        if (request.getStatus() != null) {
            wrapper.eq(UserEntity::getStatus, request.getStatus());
        }

        // Pagination
        Page<UserEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<UserEntity> result = userRepository.selectPage(page, wrapper);

        // Convert to DTOs
        List<UserDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(dtoList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(UserEntity entity) {
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
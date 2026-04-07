package com.example.demo.domain.service;

import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserCreateRequest;
import com.example.demo.api.request.UserQueryRequest;
import com.example.demo.api.request.UserUpdateRequest;
import com.example.demo.api.Response;
import com.example.demo.common.PageResult;

/**
 * User service interface
 */
public interface UserService {

    /**
     * Create user
     */
    Response<UserDTO> create(UserCreateRequest request);

    /**
     * Get user by ID
     */
    Response<UserDTO> getById(Long id);

    /**
     * Update user
     */
    Response<UserDTO> update(Long id, UserUpdateRequest request);

    /**
     * Delete user
     */
    Response<Void> delete(Long id);

    /**
     * List users with pagination
     */
    Response<PageResult<UserDTO>> list(UserQueryRequest request);
}
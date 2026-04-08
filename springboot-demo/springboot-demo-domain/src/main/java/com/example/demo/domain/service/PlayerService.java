package com.example.demo.domain.service;

import com.example.demo.api.PlayerDTO;
import com.example.demo.api.PlayerEnumsResponse;
import com.example.demo.api.request.PlayerQueryRequest;
import com.example.demo.api.request.PlayerCreateRequest;
import com.example.demo.api.request.PlayerUpdateRequest;
import com.example.demo.api.Response;
import com.example.demo.common.PageResult;

/**
 * Player service interface
 */
public interface PlayerService {

    /**
     * Get all enums for filter options
     */
    Response<PlayerEnumsResponse> getEnums();

    /**
     * List players with pagination and filters
     */
    Response<PageResult<PlayerDTO>> list(PlayerQueryRequest request);

    /**
     * Get player by ID
     */
    Response<PlayerDTO> getById(Long id);

    /**
     * Create player
     */
    Response<PlayerDTO> create(PlayerCreateRequest request);

    /**
     * Update player
     */
    Response<PlayerDTO> update(Long id, PlayerUpdateRequest request);

    /**
     * Delete player
     */
    Response<Void> delete(Long id, String password);
}
package com.example.demo.controller;

import com.example.demo.api.Response;
import com.example.demo.api.PlayerDTO;
import com.example.demo.api.PlayerEnumsResponse;
import com.example.demo.api.request.PlayerQueryRequest;
import com.example.demo.api.request.PlayerCreateRequest;
import com.example.demo.api.request.PlayerUpdateRequest;
import com.example.demo.common.PageResult;
import com.example.demo.domain.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Player REST Controller
 */
@RestController
@RequestMapping("/api/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    /**
     * Get all enums for filter options
     */
    @GetMapping("/enums")
    public Response<PlayerEnumsResponse> getEnums() {
        return playerService.getEnums();
    }

    /**
     * List players with pagination and filters
     */
    @GetMapping
    public Response<PageResult<PlayerDTO>> list(@ModelAttribute PlayerQueryRequest request) {
        return playerService.list(request);
    }

    /**
     * Get player by ID
     */
    @GetMapping("/{id}")
    public Response<PlayerDTO> getById(@PathVariable Long id) {
        return playerService.getById(id);
    }

    /**
     * Create player
     */
    @PostMapping
    public Response<PlayerDTO> create(@Valid @RequestBody PlayerCreateRequest request) {
        return playerService.create(request);
    }

    /**
     * Update player
     */
    @PostMapping("/{id}")
    public Response<PlayerDTO> update(@PathVariable Long id, @Valid @RequestBody PlayerUpdateRequest request) {
        return playerService.update(id, request);
    }

    /**
     * Delete player
     */
    @PostMapping("/delete/{id}")
    public Response<Void> delete(@PathVariable Long id, @RequestBody(required = false) java.util.Map<String, String> body) {
        String password = body != null ? body.get("password") : null;
        return playerService.delete(id, password);
    }
}
package com.example.demo.controller;

import com.example.demo.api.Response;
import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserCreateRequest;
import com.example.demo.api.request.UserQueryRequest;
import com.example.demo.api.request.UserUpdateRequest;
import com.example.demo.common.PageResult;
import com.example.demo.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * User REST Controller
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public Response<UserDTO> create(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @GetMapping("/{id}")
    public Response<UserDTO> getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public Response<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        return userService.delete(id);
    }

    @GetMapping
    public Response<PageResult<UserDTO>> list(@ModelAttribute UserQueryRequest request) {
        return userService.list(request);
    }
}
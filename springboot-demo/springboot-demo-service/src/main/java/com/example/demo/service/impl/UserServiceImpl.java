package com.example.demo.service.impl;

import com.example.demo.api.Response;
import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserCreateRequest;
import com.example.demo.api.request.UserQueryRequest;
import com.example.demo.api.request.UserUpdateRequest;
import com.example.demo.common.PageResult;
import com.example.demo.common.ResultCode;
import com.example.demo.domain.service.UserService;
import com.example.demo.service.executor.UserCreateExe;
import com.example.demo.service.executor.UserQueryExe;
import com.example.demo.service.executor.UserUpdateExe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User service implementation
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserCreateExe userCreateExe;

    @Autowired
    private UserUpdateExe userUpdateExe;

    @Autowired
    private UserQueryExe userQueryExe;

    @Override
    public Response<UserDTO> create(UserCreateRequest request) {
        try {
            UserDTO dto = userCreateExe.execute(request);
            return Response.success(dto);
        } catch (RuntimeException e) {
            return Response.fail(ResultCode.USER_ALREADY_EXISTS.getCode(), e.getMessage());
        }
    }

    @Override
    public Response<UserDTO> getById(Long id) {
        UserDTO dto = userQueryExe.getById(id);
        if (dto == null) {
            return Response.fail(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMessage());
        }
        return Response.success(dto);
    }

    @Override
    public Response<UserDTO> update(Long id, UserUpdateRequest request) {
        try {
            UserDTO dto = userUpdateExe.execute(id, request);
            return Response.success(dto);
        } catch (RuntimeException e) {
            return Response.fail(ResultCode.USER_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @Override
    public Response<Void> delete(Long id) {
        // Check if exists first
        UserDTO dto = userQueryExe.getById(id);
        if (dto == null) {
            return Response.fail(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMessage());
        }
        // Delete (logical delete via MyBatis Plus)
        userQueryExe.deleteById(id);
        return Response.success();
    }

    @Override
    public Response<PageResult<UserDTO>> list(UserQueryRequest request) {
        PageResult<UserDTO> result = userQueryExe.list(request);
        return Response.success(result);
    }
}
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * User service implementation
 * 缓存策略：
 * - getById：缓存单个用户，key = user::{id}，TTL 10 分钟
 * - update/delete：操作成功后清除对应缓存
 * - create/list：不缓存（数据变化频繁，缓存意义不大）
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

    /**
     * 查询单个用户，结果缓存到 Redis
     * key = "user::{id}"，如：user::1
     * unless = "#result.data == null" 表示查不到用户时不缓存
     */
    @Override
    @Cacheable(value = "user", key = "#id", unless = "#result.data == null")
    public Response<UserDTO> getById(Long id) {
        UserDTO dto = userQueryExe.getById(id);
        if (dto == null) {
            return Response.fail(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMessage());
        }
        return Response.success(dto);
    }

    /**
     * 更新用户，成功后清除该用户的缓存
     */
    @Override
    @CacheEvict(value = "user", key = "#id")
    public Response<UserDTO> update(Long id, UserUpdateRequest request) {
        try {
            UserDTO dto = userUpdateExe.execute(id, request);
            return Response.success(dto);
        } catch (RuntimeException e) {
            return Response.fail(ResultCode.USER_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    /**
     * 删除用户，成功后清除该用户的缓存
     */
    @Override
    @CacheEvict(value = "user", key = "#id")
    public Response<Void> delete(Long id) {
        UserDTO dto = userQueryExe.getById(id);
        if (dto == null) {
            return Response.fail(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMessage());
        }
        userQueryExe.deleteById(id);
        return Response.success();
    }

    @Override
    public Response<PageResult<UserDTO>> list(UserQueryRequest request) {
        PageResult<UserDTO> result = userQueryExe.list(request);
        return Response.success(result);
    }
}
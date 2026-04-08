package com.example.demo.service.impl;

import com.example.demo.api.Response;
import com.example.demo.api.PlayerDTO;
import com.example.demo.api.PlayerEnumsResponse;
import com.example.demo.api.request.PlayerQueryRequest;
import com.example.demo.api.request.PlayerCreateRequest;
import com.example.demo.api.request.PlayerUpdateRequest;
import com.example.demo.common.PageResult;
import com.example.demo.common.ResultCode;
import com.example.demo.domain.service.PlayerService;
import com.example.demo.service.executor.PlayerCreateExe;
import com.example.demo.service.executor.PlayerQueryExe;
import com.example.demo.service.executor.PlayerUpdateExe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Player service implementation
 */
@Service
public class PlayerServiceImpl implements PlayerService {

    // 密码的 MD5 值
    private static final String PASSWORD_MD5 = "6b09e658e9143361008d26983cc738ec";

    @Autowired
    private PlayerCreateExe playerCreateExe;

    @Autowired
    private PlayerUpdateExe playerUpdateExe;

    @Autowired
    private PlayerQueryExe playerQueryExe;

    @Override
    public Response<PlayerEnumsResponse> getEnums() {
        PlayerEnumsResponse enums = playerQueryExe.getEnums();
        return Response.success(enums);
    }

    @Override
    public Response<PageResult<PlayerDTO>> list(PlayerQueryRequest request) {
        PageResult<PlayerDTO> result = playerQueryExe.list(request);
        return Response.success(result);
    }

    @Override
    public Response<PlayerDTO> getById(Long id) {
        PlayerDTO dto = playerQueryExe.getById(id);
        if (dto == null) {
            return Response.fail(ResultCode.PLAYER_NOT_FOUND.getCode(), ResultCode.PLAYER_NOT_FOUND.getMessage());
        }
        return Response.success(dto);
    }

    @Override
    public Response<PlayerDTO> create(PlayerCreateRequest request) {
        // Validate password - 使用 MD5 验证
        if (!PASSWORD_MD5.equals(request.getPassword())) {
            return Response.fail(ResultCode.PLAYER_PASSWORD_INVALID.getCode(), ResultCode.PLAYER_PASSWORD_INVALID.getMessage());
        }

        PlayerDTO dto = playerCreateExe.execute(request);
        return Response.success(dto);
    }

    @Override
    public Response<PlayerDTO> update(Long id, PlayerUpdateRequest request) {
        // Validate password - 使用 MD5 验证
        if (!PASSWORD_MD5.equals(request.getPassword())) {
            return Response.fail(ResultCode.PLAYER_PASSWORD_INVALID.getCode(), ResultCode.PLAYER_PASSWORD_INVALID.getMessage());
        }

        try {
            PlayerDTO dto = playerUpdateExe.execute(id, request);
            return Response.success(dto);
        } catch (RuntimeException e) {
            return Response.fail(ResultCode.PLAYER_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @Override
    public Response<Void> delete(Long id, String password) {
        // Validate password - 使用 MD5 验证
        if (!PASSWORD_MD5.equals(password)) {
            return Response.fail(ResultCode.PLAYER_PASSWORD_INVALID.getCode(), ResultCode.PLAYER_PASSWORD_INVALID.getMessage());
        }

        PlayerDTO dto = playerQueryExe.getById(id);
        if (dto == null) {
            return Response.fail(ResultCode.PLAYER_NOT_FOUND.getCode(), ResultCode.PLAYER_NOT_FOUND.getMessage());
        }

        playerQueryExe.deleteById(id);
        return Response.success();
    }
}
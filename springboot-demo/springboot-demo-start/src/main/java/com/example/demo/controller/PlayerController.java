package com.example.demo.controller;

import com.example.demo.api.Response;
import com.example.demo.api.PlayerDTO;
import com.example.demo.api.PlayerEnumsResponse;
import com.example.demo.api.request.PlayerQueryRequest;
import com.example.demo.api.request.PlayerCreateRequest;
import com.example.demo.api.request.PlayerUpdateRequest;
import com.example.demo.common.PageResult;
import com.example.demo.domain.service.PlayerService;
import com.example.demo.service.OssUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * Player REST Controller
 */
@RestController
@RequestMapping("/api/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private OssUploadService ossUploadService;

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

    /**
     * 上传球员头像到 OSS，返回图片访问 URL
     */
    @PostMapping("/{id}/avatar")
    public Response<String> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Response.fail("只允许上传图片文件");
        }
        // 校验文件大小（限制 2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            return Response.fail("图片大小不能超过 2MB");
        }
        try {
            // 只上传到 OSS 返回 URL，不立即更新数据库
            // 用户点击保存时，cardImage 字段会随其他字段一起提交更新
            String imageUrl = ossUploadService.uploadPlayerAvatar(file, id);
            return Response.success(imageUrl);
        } catch (Exception e) {
            return Response.fail("头像上传失败：" + e.getMessage());
        }
    }
}
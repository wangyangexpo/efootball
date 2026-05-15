package com.example.demo.controller;

import com.example.demo.api.ChangeRequestDTO;
import com.example.demo.api.Response;
import com.example.demo.api.request.ReviewRequest;
import com.example.demo.domain.service.ChangeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员审批接口(经 AdminAuthInterceptor 保护)
 */
@RestController
@RequestMapping("/api/admin/change-request")
public class AdminReviewController {

    @Autowired
    private ChangeRequestService changeRequestService;

    @PostMapping("/{id}/approve")
    public Response<ChangeRequestDTO> approve(@PathVariable Long id,
                                              @RequestBody(required = false) ReviewRequest request) {
        return changeRequestService.approve(id, request);
    }

    @PostMapping("/{id}/reject")
    public Response<ChangeRequestDTO> reject(@PathVariable Long id,
                                             @RequestBody(required = false) ReviewRequest request) {
        return changeRequestService.reject(id, request);
    }
}

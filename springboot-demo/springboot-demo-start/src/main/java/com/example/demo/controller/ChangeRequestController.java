package com.example.demo.controller;

import com.example.demo.api.ChangeRequestDTO;
import com.example.demo.api.Response;
import com.example.demo.api.request.ChangeRequestQueryRequest;
import com.example.demo.api.request.ChangeRequestSubmitRequest;
import com.example.demo.api.request.VoteRequest;
import com.example.demo.common.PageResult;
import com.example.demo.domain.service.ChangeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 公开接口:任何访客可访问
 */
@RestController
@RequestMapping("/api/change-request")
public class ChangeRequestController {

    @Autowired
    private ChangeRequestService changeRequestService;

    @PostMapping
    public Response<ChangeRequestDTO> submit(@Valid @RequestBody ChangeRequestSubmitRequest request) {
        return changeRequestService.submit(request);
    }

    @GetMapping
    public Response<PageResult<ChangeRequestDTO>> list(@ModelAttribute ChangeRequestQueryRequest request) {
        return changeRequestService.list(request);
    }

    @GetMapping("/{id}")
    public Response<ChangeRequestDTO> getById(@PathVariable Long id,
                                              @RequestParam(required = false) String voterId) {
        return changeRequestService.getById(id, voterId);
    }

    @PostMapping("/{id}/vote")
    public Response<ChangeRequestDTO> vote(@PathVariable Long id,
                                           @Valid @RequestBody VoteRequest request) {
        return changeRequestService.vote(id, request);
    }
}

package com.example.demo.service.impl;

import com.example.demo.api.ChangeRequestDTO;
import com.example.demo.api.Response;
import com.example.demo.api.request.ChangeRequestQueryRequest;
import com.example.demo.api.request.ChangeRequestSubmitRequest;
import com.example.demo.api.request.ReviewRequest;
import com.example.demo.api.request.VoteRequest;
import com.example.demo.common.PageResult;
import com.example.demo.domain.service.ChangeRequestService;
import com.example.demo.service.executor.BizException;
import com.example.demo.service.executor.ChangeRequestQueryExe;
import com.example.demo.service.executor.ChangeRequestReviewExe;
import com.example.demo.service.executor.ChangeRequestSubmitExe;
import com.example.demo.service.executor.ChangeRequestVoteExe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * 修改提议服务实现
 *
 * 缓存策略说明:
 *   - 列表/详情查询故意不加 @Cacheable;投票频率较高,实时性优先于缓存收益
 *   - 采纳会改写 player 表,所以连带清理 playerList / player / playerEnums 缓存
 */
@Service
public class ChangeRequestServiceImpl implements ChangeRequestService {

    @Autowired
    private ChangeRequestSubmitExe submitExe;

    @Autowired
    private ChangeRequestQueryExe queryExe;

    @Autowired
    private ChangeRequestVoteExe voteExe;

    @Autowired
    private ChangeRequestReviewExe reviewExe;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public Response<ChangeRequestDTO> submit(ChangeRequestSubmitRequest request) {
        try {
            return Response.success(submitExe.execute(request));
        } catch (BizException e) {
            return Response.fail(e.getCode(), e.getMessage());
        }
    }

    @Override
    public Response<PageResult<ChangeRequestDTO>> list(ChangeRequestQueryRequest request) {
        return Response.success(queryExe.list(request));
    }

    @Override
    public Response<ChangeRequestDTO> getById(Long id, String voterId) {
        try {
            return Response.success(queryExe.getById(id, voterId));
        } catch (BizException e) {
            return Response.fail(e.getCode(), e.getMessage());
        }
    }

    @Override
    public Response<ChangeRequestDTO> vote(Long id, VoteRequest request) {
        try {
            return Response.success(voteExe.execute(id, request));
        } catch (BizException e) {
            return Response.fail(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "playerList", allEntries = true),
            @CacheEvict(value = "playerEnums", allEntries = true)
    })
    public Response<ChangeRequestDTO> approve(Long id, ReviewRequest request) {
        try {
            ChangeRequestDTO dto = reviewExe.approve(id, request);
            // 单独清理目标球员的缓存(直接 CacheManager,避免自调用绕过 AOP)
            Cache playerCache = cacheManager.getCache("player");
            if (playerCache != null && dto.getPlayerId() != null) {
                playerCache.evict(dto.getPlayerId());
            }
            return Response.success(dto);
        } catch (BizException e) {
            return Response.fail(e.getCode(), e.getMessage());
        }
    }

    @Override
    public Response<ChangeRequestDTO> reject(Long id, ReviewRequest request) {
        try {
            return Response.success(reviewExe.reject(id, request));
        } catch (BizException e) {
            return Response.fail(e.getCode(), e.getMessage());
        }
    }
}

package com.example.demo.domain.service;

import com.example.demo.api.ChangeRequestDTO;
import com.example.demo.api.Response;
import com.example.demo.api.request.ChangeRequestQueryRequest;
import com.example.demo.api.request.ChangeRequestSubmitRequest;
import com.example.demo.api.request.ReviewRequest;
import com.example.demo.api.request.VoteRequest;
import com.example.demo.common.PageResult;

/**
 * 修改提议服务接口
 */
public interface ChangeRequestService {

    /** 任意访客提交修改提议 */
    Response<ChangeRequestDTO> submit(ChangeRequestSubmitRequest request);

    /** 列表(默认 pending,按 approve_count 降序) */
    Response<PageResult<ChangeRequestDTO>> list(ChangeRequestQueryRequest request);

    /** 单条详情 */
    Response<ChangeRequestDTO> getById(Long id, String voterId);

    /** 投票:重复点同方向 = 取消;切方向 = 改投 */
    Response<ChangeRequestDTO> vote(Long id, VoteRequest request);

    /** 管理员采纳:proposed_* 写回 player 表 */
    Response<ChangeRequestDTO> approve(Long id, ReviewRequest request);

    /** 管理员驳回 */
    Response<ChangeRequestDTO> reject(Long id, ReviewRequest request);
}

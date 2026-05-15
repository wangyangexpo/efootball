package com.example.demo.service.executor;

import com.example.demo.api.ChangeRequestDTO;
import com.example.demo.api.request.VoteRequest;
import com.example.demo.common.ResultCode;
import com.example.demo.domain.entity.ChangeRequestEntity;
import com.example.demo.domain.entity.ChangeVoteEntity;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.ChangeRequestRepository;
import com.example.demo.repository.ChangeVoteRepository;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 投票执行器
 *
 * 三种行为:
 *   1. 首次投票(approve / disapprove):插入 vote,对应计数 +1
 *   2. 重复同方向投票:删除 vote,对应计数 -1(允许取消)
 *   3. 切换方向:更新 vote,旧计数 -1, 新计数 +1
 */
@Component
public class ChangeRequestVoteExe {

    @Autowired
    private ChangeRequestRepository changeRequestRepository;

    @Autowired
    private ChangeVoteRepository changeVoteRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public ChangeRequestDTO execute(Long requestId, VoteRequest request) {
        if (request.getVoteType() != 1 && request.getVoteType() != -1) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "voteType 必须为 1 或 -1");
        }

        ChangeRequestEntity entity = changeRequestRepository.selectById(requestId);
        if (entity == null) {
            throw new BizException(ResultCode.CHANGE_REQUEST_NOT_FOUND.getCode(),
                    ResultCode.CHANGE_REQUEST_NOT_FOUND.getMessage());
        }
        if (!"pending".equals(entity.getStatus())) {
            throw new BizException(ResultCode.CHANGE_REQUEST_NOT_PENDING.getCode(),
                    ResultCode.CHANGE_REQUEST_NOT_PENDING.getMessage());
        }

        ChangeVoteEntity existing = changeVoteRepository.findByRequestAndVoter(requestId, request.getVoterId());
        Integer myVoteAfter;

        if (existing == null) {
            ChangeVoteEntity vote = new ChangeVoteEntity();
            vote.setRequestId(requestId);
            vote.setVoterId(request.getVoterId());
            vote.setVoteType(request.getVoteType());
            changeVoteRepository.insert(vote);
            adjustCount(entity, request.getVoteType(), 1);
            myVoteAfter = request.getVoteType();
        } else if (existing.getVoteType().equals(request.getVoteType())) {
            // 同方向再点 = 取消
            changeVoteRepository.deleteById(existing.getId());
            adjustCount(entity, existing.getVoteType(), -1);
            myVoteAfter = null;
        } else {
            // 切换方向
            existing.setVoteType(request.getVoteType());
            changeVoteRepository.updateById(existing);
            adjustCount(entity, existing.getVoteType(), 1);
            // existing.voteType 已被覆盖,旧值用 request 反推
            int oldVote = -request.getVoteType();
            adjustCount(entity, oldVote, -1);
            myVoteAfter = request.getVoteType();
        }

        changeRequestRepository.updateById(entity);

        PlayerEntity player = playerRepository.selectById(entity.getPlayerId());
        return ChangeRequestConverter.toDTO(entity, player, myVoteAfter);
    }

    private void adjustCount(ChangeRequestEntity entity, int voteType, int delta) {
        if (voteType == 1) {
            entity.setApproveCount(Math.max(0, entity.getApproveCount() + delta));
        } else if (voteType == -1) {
            entity.setDisapproveCount(Math.max(0, entity.getDisapproveCount() + delta));
        }
    }
}

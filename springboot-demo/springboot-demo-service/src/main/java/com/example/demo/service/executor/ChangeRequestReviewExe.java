package com.example.demo.service.executor;

import com.example.demo.api.ChangeRequestDTO;
import com.example.demo.api.request.ReviewRequest;
import com.example.demo.common.ResultCode;
import com.example.demo.domain.entity.ChangeRequestEntity;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.ChangeRequestRepository;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理员审批执行器(采纳 / 驳回)
 */
@Component
public class ChangeRequestReviewExe {

    @Autowired
    private ChangeRequestRepository changeRequestRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public ChangeRequestDTO approve(Long requestId, ReviewRequest req) {
        ChangeRequestEntity entity = loadPending(requestId);
        PlayerEntity player = playerRepository.selectById(entity.getPlayerId());
        if (player == null) {
            throw new BizException(ResultCode.PLAYER_NOT_FOUND.getCode(),
                    ResultCode.PLAYER_NOT_FOUND.getMessage());
        }

        // 把非空 proposed_* 写回 player 表
        if (entity.getProposedName() != null) player.setName(entity.getProposedName());
        if (entity.getProposedPosition() != null) player.setPosition(entity.getProposedPosition());
        if (entity.getProposedStatus() != null) player.setStatus(entity.getProposedStatus());
        if (entity.getProposedNumber() != null) player.setNumber(entity.getProposedNumber());
        if (entity.getProposedClub() != null) player.setClub(entity.getProposedClub());
        if (entity.getProposedLeague() != null) player.setLeague(entity.getProposedLeague());
        if (entity.getProposedCountry() != null) player.setCountry(entity.getProposedCountry());
        if (entity.getProposedHeight() != null) player.setHeight(entity.getProposedHeight());
        if (entity.getProposedFoot() != null) player.setFoot(entity.getProposedFoot());
        player.setUpdateTime(LocalDateTime.now());
        playerRepository.updateById(player);

        entity.setStatus("approved");
        changeRequestRepository.updateById(entity);

        return ChangeRequestConverter.toDTO(entity, player, null);
    }

    @Transactional
    public ChangeRequestDTO reject(Long requestId, ReviewRequest req) {
        ChangeRequestEntity entity = loadPending(requestId);
        entity.setStatus("rejected");
        if (req != null && req.getReason() != null) {
            entity.setRejectReason(req.getReason());
        }
        changeRequestRepository.updateById(entity);
        PlayerEntity player = playerRepository.selectById(entity.getPlayerId());
        return ChangeRequestConverter.toDTO(entity, player, null);
    }

    private ChangeRequestEntity loadPending(Long id) {
        ChangeRequestEntity entity = changeRequestRepository.selectById(id);
        if (entity == null) {
            throw new BizException(ResultCode.CHANGE_REQUEST_NOT_FOUND.getCode(),
                    ResultCode.CHANGE_REQUEST_NOT_FOUND.getMessage());
        }
        if (!"pending".equals(entity.getStatus())) {
            throw new BizException(ResultCode.CHANGE_REQUEST_NOT_PENDING.getCode(),
                    ResultCode.CHANGE_REQUEST_NOT_PENDING.getMessage());
        }
        return entity;
    }
}

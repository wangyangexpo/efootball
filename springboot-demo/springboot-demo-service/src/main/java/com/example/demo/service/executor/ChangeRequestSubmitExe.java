package com.example.demo.service.executor;

import com.example.demo.api.ChangeRequestDTO;
import com.example.demo.api.request.ChangeRequestSubmitRequest;
import com.example.demo.common.ResultCode;
import com.example.demo.domain.entity.ChangeRequestEntity;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.ChangeRequestRepository;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 提交修改提议执行器
 */
@Component
public class ChangeRequestSubmitExe {

    @Autowired
    private ChangeRequestRepository changeRequestRepository;

    @Autowired
    private PlayerRepository playerRepository;

    /**
     * @return ChangeRequestDTO 成功返回提议 DTO;失败抛 BizException 由上层翻译为 Response
     */
    public ChangeRequestDTO execute(ChangeRequestSubmitRequest request) {
        PlayerEntity player = playerRepository.selectById(request.getPlayerId());
        if (player == null) {
            throw new BizException(ResultCode.PLAYER_NOT_FOUND.getCode(),
                    ResultCode.PLAYER_NOT_FOUND.getMessage());
        }

        // diff 校验:至少一个 proposed 字段与 player 当前值不同(忽略 proposed 为 null 的字段)
        if (!hasAnyDiff(request, player)) {
            throw new BizException(ResultCode.CHANGE_REQUEST_NO_DIFF.getCode(),
                    ResultCode.CHANGE_REQUEST_NO_DIFF.getMessage());
        }

        // 同 (player, submitter) pending 唯一
        if (changeRequestRepository.findPending(player.getId(), request.getSubmitterId()) != null) {
            throw new BizException(ResultCode.CHANGE_REQUEST_DUPLICATE_PENDING.getCode(),
                    ResultCode.CHANGE_REQUEST_DUPLICATE_PENDING.getMessage());
        }

        ChangeRequestEntity entity = new ChangeRequestEntity();
        entity.setPlayerId(player.getId());
        entity.setSubmitterId(request.getSubmitterId());
        entity.setProposedName(normalize(request.getProposedName(), player.getName()));
        entity.setProposedPosition(normalize(request.getProposedPosition(), player.getPosition()));
        entity.setProposedStatus(normalize(request.getProposedStatus(), player.getStatus()));
        entity.setProposedNumber(normalize(request.getProposedNumber(), player.getNumber()));
        entity.setProposedClub(normalize(request.getProposedClub(), player.getClub()));
        entity.setProposedLeague(normalize(request.getProposedLeague(), player.getLeague()));
        entity.setProposedCountry(normalize(request.getProposedCountry(), player.getCountry()));
        entity.setProposedHeight(normalize(request.getProposedHeight(), player.getHeight()));
        entity.setProposedFoot(normalize(request.getProposedFoot(), player.getFoot()));
        entity.setApproveCount(0);
        entity.setDisapproveCount(0);
        entity.setStatus("pending");
        entity.setDeleted(0);

        changeRequestRepository.insert(entity);

        return ChangeRequestConverter.toDTO(entity, player, null);
    }

    private boolean hasAnyDiff(ChangeRequestSubmitRequest req, PlayerEntity p) {
        return diff(req.getProposedName(), p.getName())
                || diff(req.getProposedPosition(), p.getPosition())
                || diff(req.getProposedStatus(), p.getStatus())
                || diff(req.getProposedNumber(), p.getNumber())
                || diff(req.getProposedClub(), p.getClub())
                || diff(req.getProposedLeague(), p.getLeague())
                || diff(req.getProposedCountry(), p.getCountry())
                || diff(req.getProposedHeight(), p.getHeight())
                || diff(req.getProposedFoot(), p.getFoot());
    }

    private boolean diff(Object proposed, Object current) {
        if (proposed == null) {
            return false;
        }
        return !Objects.equals(proposed, current);
    }

    /**
     * 提议为空时回填当前值,确保入库后每条记录都有完整快照,便于后续 diff 展示
     */
    private <T> T normalize(T proposed, T current) {
        return proposed != null ? proposed : current;
    }
}

package com.example.demo.service.executor;

import com.example.demo.api.ChangeRequestDTO;
import com.example.demo.domain.entity.ChangeRequestEntity;
import com.example.demo.domain.entity.PlayerEntity;

/**
 * 修改提议转换器
 */
public final class ChangeRequestConverter {

    private ChangeRequestConverter() {
    }

    /**
     * 将 entity + 关联 player 当前快照 + voter 投票状态合并为 DTO
     *
     * @param entity   提议实体
     * @param player   关联球员当前快照(可空,空时不填快照字段)
     * @param myVote   当前访客对该提议的投票:1 / -1 / null
     */
    public static ChangeRequestDTO toDTO(ChangeRequestEntity entity, PlayerEntity player, Integer myVote) {
        if (entity == null) {
            return null;
        }
        ChangeRequestDTO dto = new ChangeRequestDTO();
        dto.setId(entity.getId());
        dto.setPlayerId(entity.getPlayerId());
        dto.setSubmitterId(entity.getSubmitterId());
        dto.setProposedName(entity.getProposedName());
        dto.setProposedPosition(entity.getProposedPosition());
        dto.setProposedStatus(entity.getProposedStatus());
        dto.setProposedNumber(entity.getProposedNumber());
        dto.setProposedClub(entity.getProposedClub());
        dto.setProposedLeague(entity.getProposedLeague());
        dto.setProposedCountry(entity.getProposedCountry());
        dto.setProposedHeight(entity.getProposedHeight());
        dto.setProposedFoot(entity.getProposedFoot());

        if (player != null) {
            dto.setCurrentName(player.getName());
            dto.setCurrentPosition(player.getPosition());
            dto.setCurrentStatus(player.getStatus());
            dto.setCurrentNumber(player.getNumber());
            dto.setCurrentClub(player.getClub());
            dto.setCurrentLeague(player.getLeague());
            dto.setCurrentCountry(player.getCountry());
            dto.setCurrentHeight(player.getHeight());
            dto.setCurrentFoot(player.getFoot());
            dto.setCurrentCardImage(player.getCardImage());
        }

        dto.setApproveCount(entity.getApproveCount());
        dto.setDisapproveCount(entity.getDisapproveCount());
        dto.setStatus(entity.getStatus());
        dto.setRejectReason(entity.getRejectReason());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        dto.setMyVote(myVote);

        return dto;
    }
}

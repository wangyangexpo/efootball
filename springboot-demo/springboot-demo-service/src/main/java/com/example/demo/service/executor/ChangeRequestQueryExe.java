package com.example.demo.service.executor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.api.ChangeRequestDTO;
import com.example.demo.api.request.ChangeRequestQueryRequest;
import com.example.demo.common.PageResult;
import com.example.demo.common.ResultCode;
import com.example.demo.domain.entity.ChangeRequestEntity;
import com.example.demo.domain.entity.ChangeVoteEntity;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.ChangeRequestRepository;
import com.example.demo.repository.ChangeVoteRepository;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 修改提议查询执行器
 */
@Component
public class ChangeRequestQueryExe {

    @Autowired
    private ChangeRequestRepository changeRequestRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ChangeVoteRepository changeVoteRepository;

    public PageResult<ChangeRequestDTO> list(ChangeRequestQueryRequest request) {
        LambdaQueryWrapper<ChangeRequestEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChangeRequestEntity::getStatus, request.getStatus())
                .orderByDesc(ChangeRequestEntity::getApproveCount)
                .orderByDesc(ChangeRequestEntity::getId);

        Page<ChangeRequestEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<ChangeRequestEntity> result = changeRequestRepository.selectPage(page, wrapper);

        List<ChangeRequestEntity> records = result.getRecords();

        Map<Long, PlayerEntity> playerMap = loadPlayerMap(records);
        Map<Long, Integer> voteMap = loadVoteMap(records, request.getVoterId());

        List<ChangeRequestDTO> list = records.stream()
                .map(e -> ChangeRequestConverter.toDTO(e, playerMap.get(e.getPlayerId()), voteMap.get(e.getId())))
                .collect(Collectors.toList());

        return PageResult.of(list, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    public ChangeRequestDTO getById(Long id, String voterId) {
        ChangeRequestEntity entity = changeRequestRepository.selectById(id);
        if (entity == null) {
            throw new BizException(ResultCode.CHANGE_REQUEST_NOT_FOUND.getCode(),
                    ResultCode.CHANGE_REQUEST_NOT_FOUND.getMessage());
        }
        PlayerEntity player = playerRepository.selectById(entity.getPlayerId());
        Integer myVote = null;
        if (voterId != null && !voterId.isEmpty()) {
            ChangeVoteEntity vote = changeVoteRepository.findByRequestAndVoter(id, voterId);
            if (vote != null) {
                myVote = vote.getVoteType();
            }
        }
        return ChangeRequestConverter.toDTO(entity, player, myVote);
    }

    private Map<Long, PlayerEntity> loadPlayerMap(List<ChangeRequestEntity> records) {
        Set<Long> playerIds = records.stream()
                .map(ChangeRequestEntity::getPlayerId)
                .collect(Collectors.toSet());
        if (playerIds.isEmpty()) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<PlayerEntity> playerWrapper = new LambdaQueryWrapper<>();
        playerWrapper.in(PlayerEntity::getId, playerIds);
        return playerRepository.selectList(playerWrapper).stream()
                .collect(Collectors.toMap(PlayerEntity::getId, Function.identity()));
    }

    private Map<Long, Integer> loadVoteMap(List<ChangeRequestEntity> records, String voterId) {
        if (voterId == null || voterId.isEmpty() || records.isEmpty()) {
            return new HashMap<>();
        }
        Set<Long> requestIds = records.stream()
                .map(ChangeRequestEntity::getId)
                .collect(Collectors.toSet());
        return changeVoteRepository.findByVoterAndRequests(voterId, requestIds).stream()
                .collect(Collectors.toMap(ChangeVoteEntity::getRequestId, ChangeVoteEntity::getVoteType));
    }
}

package com.example.demo.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.domain.entity.ChangeVoteEntity;
import com.example.demo.repository.mapper.ChangeVoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 投票仓储
 */
@Repository
public class ChangeVoteRepository {

    @Autowired
    private ChangeVoteMapper changeVoteMapper;

    public ChangeVoteEntity findByRequestAndVoter(Long requestId, String voterId) {
        LambdaQueryWrapper<ChangeVoteEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChangeVoteEntity::getRequestId, requestId)
                .eq(ChangeVoteEntity::getVoterId, voterId)
                .last("LIMIT 1");
        return changeVoteMapper.selectOne(wrapper);
    }

    public int insert(ChangeVoteEntity entity) {
        return changeVoteMapper.insert(entity);
    }

    public int updateById(ChangeVoteEntity entity) {
        return changeVoteMapper.updateById(entity);
    }

    public int deleteById(Long id) {
        return changeVoteMapper.deleteById(id);
    }

    /**
     * 批量查指定 voterId 在一组 requestId 上的投票
     */
    public List<ChangeVoteEntity> findByVoterAndRequests(String voterId, Collection<Long> requestIds) {
        if (requestIds == null || requestIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ChangeVoteEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChangeVoteEntity::getVoterId, voterId)
                .in(ChangeVoteEntity::getRequestId, requestIds);
        return changeVoteMapper.selectList(wrapper);
    }
}

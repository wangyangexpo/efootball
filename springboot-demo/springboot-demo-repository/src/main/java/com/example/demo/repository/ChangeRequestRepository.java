package com.example.demo.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.domain.entity.ChangeRequestEntity;
import com.example.demo.repository.mapper.ChangeRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 修改提议仓储
 */
@Repository
public class ChangeRequestRepository {

    @Autowired
    private ChangeRequestMapper changeRequestMapper;

    public ChangeRequestEntity selectById(Long id) {
        return changeRequestMapper.selectById(id);
    }

    public int insert(ChangeRequestEntity entity) {
        return changeRequestMapper.insert(entity);
    }

    public int updateById(ChangeRequestEntity entity) {
        return changeRequestMapper.updateById(entity);
    }

    public Page<ChangeRequestEntity> selectPage(Page<ChangeRequestEntity> page,
                                                LambdaQueryWrapper<ChangeRequestEntity> wrapper) {
        return changeRequestMapper.selectPage(page, wrapper);
    }

    public List<ChangeRequestEntity> selectList(LambdaQueryWrapper<ChangeRequestEntity> wrapper) {
        return changeRequestMapper.selectList(wrapper);
    }

    public List<ChangeRequestEntity> selectByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return changeRequestMapper.selectBatchIds(ids);
    }

    /**
     * 查询是否存在 (player, submitter) 的 pending 提议
     */
    public ChangeRequestEntity findPending(Long playerId, String submitterId) {
        LambdaQueryWrapper<ChangeRequestEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChangeRequestEntity::getPlayerId, playerId)
                .eq(ChangeRequestEntity::getSubmitterId, submitterId)
                .eq(ChangeRequestEntity::getStatus, "pending")
                .last("LIMIT 1");
        return changeRequestMapper.selectOne(wrapper);
    }
}

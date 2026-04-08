package com.example.demo.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.mapper.PlayerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Player repository abstraction
 */
@Repository
public class PlayerRepository {

    @Autowired
    private PlayerMapper playerMapper;

    public PlayerEntity selectById(Long id) {
        return playerMapper.selectById(id);
    }

    public int insert(PlayerEntity entity) {
        return playerMapper.insert(entity);
    }

    public int updateById(PlayerEntity entity) {
        return playerMapper.updateById(entity);
    }

    public int deleteById(Long id) {
        return playerMapper.deleteById(id);
    }

    public List<PlayerEntity> selectList(LambdaQueryWrapper<PlayerEntity> wrapper) {
        return playerMapper.selectList(wrapper);
    }

    public Page<PlayerEntity> selectPage(Page<PlayerEntity> page, LambdaQueryWrapper<PlayerEntity> wrapper) {
        return playerMapper.selectPage(page, wrapper);
    }

    /**
     * Select all players for enum extraction
     */
    public List<PlayerEntity> selectAll() {
        return playerMapper.selectList(new LambdaQueryWrapper<>());
    }
}
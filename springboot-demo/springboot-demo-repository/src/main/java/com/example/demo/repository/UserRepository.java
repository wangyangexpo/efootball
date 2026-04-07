package com.example.demo.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User repository abstraction
 */
@Repository
public class UserRepository {

    @Autowired
    private UserMapper userMapper;

    public UserEntity selectById(Long id) {
        return userMapper.selectById(id);
    }

    public int insert(UserEntity entity) {
        return userMapper.insert(entity);
    }

    public int updateById(UserEntity entity) {
        return userMapper.updateById(entity);
    }

    public int deleteById(Long id) {
        return userMapper.deleteById(id);
    }

    public List<UserEntity> selectList(LambdaQueryWrapper<UserEntity> wrapper) {
        return userMapper.selectList(wrapper);
    }

    public Page<UserEntity> selectPage(Page<UserEntity> page, LambdaQueryWrapper<UserEntity> wrapper) {
        return userMapper.selectPage(page, wrapper);
    }

    public UserEntity selectByUsername(String username) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
}
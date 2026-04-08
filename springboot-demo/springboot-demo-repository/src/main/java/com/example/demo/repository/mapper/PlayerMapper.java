package com.example.demo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.domain.entity.PlayerEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Player MyBatis mapper interface
 */
@Mapper
public interface PlayerMapper extends BaseMapper<PlayerEntity> {

}
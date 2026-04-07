package com.example.demo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.domain.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * User MyBatis mapper interface
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}
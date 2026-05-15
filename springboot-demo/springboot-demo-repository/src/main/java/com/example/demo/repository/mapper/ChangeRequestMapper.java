package com.example.demo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.domain.entity.ChangeRequestEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChangeRequestMapper extends BaseMapper<ChangeRequestEntity> {
}

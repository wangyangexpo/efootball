package com.example.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 修改提议投票实体
 */
@Data
@TableName("player_change_vote")
public class ChangeVoteEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long requestId;

    private String voterId;

    /** 1 = approve, -1 = disapprove */
    private Integer voteType;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

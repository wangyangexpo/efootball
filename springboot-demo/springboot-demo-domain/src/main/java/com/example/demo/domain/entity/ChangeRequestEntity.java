package com.example.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 球员数据修改提议实体
 */
@Data
@TableName("player_change_request")
public class ChangeRequestEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long playerId;

    private String submitterId;

    private String proposedName;
    private String proposedPosition;
    private String proposedStatus;
    private Integer proposedNumber;
    private String proposedClub;
    private String proposedLeague;
    private String proposedCountry;
    private Integer proposedHeight;
    private String proposedFoot;

    private Integer approveCount;

    private Integer disapproveCount;

    /** pending / approved / rejected */
    private String status;

    private String rejectReason;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}

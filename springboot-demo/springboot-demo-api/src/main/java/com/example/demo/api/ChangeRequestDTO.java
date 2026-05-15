package com.example.demo.api;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 修改提议响应 DTO
 *
 * 包含三部分数据,以避免前端 N+1 查询:
 *   1. 提议自身字段(id / proposed_* / counts / status / submitter)
 *   2. 关联球员当前快照(currentName / currentPosition / ... / currentCardImage)
 *   3. 当前访客对该提议的投票状态 myVote(传入 voterId 时返回,否则 null)
 */
@Data
public class ChangeRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long playerId;
    private String submitterId;

    // 建议字段
    private String proposedName;
    private String proposedPosition;
    private String proposedStatus;
    private Integer proposedNumber;
    private String proposedClub;
    private String proposedLeague;
    private String proposedCountry;
    private Integer proposedHeight;
    private String proposedFoot;

    // 球员当前快照(便于前端 diff 展示)
    private String currentName;
    private String currentPosition;
    private String currentStatus;
    private Integer currentNumber;
    private String currentClub;
    private String currentLeague;
    private String currentCountry;
    private Integer currentHeight;
    private String currentFoot;
    private String currentCardImage;

    private Integer approveCount;
    private Integer disapproveCount;

    private String status;
    private String rejectReason;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 当前访客的投票:1 / -1 / null(未投或未传 voterId) */
    private Integer myVote;
}

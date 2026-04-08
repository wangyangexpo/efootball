package com.example.demo.api.request;

import lombok.Data;
import java.io.Serializable;

/**
 * Player query request with pagination and filters
 */
@Data
public class PlayerQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // 球员位置
    private String position;

    // 现役状态：现役/历史
    private String status;

    // 球衣号码
    private Integer number;

    // 所在联赛
    private String league;

    // 俱乐部
    private String club;

    // 国家队
    private String country;

    // 惯用脚
    private String foot;

    // 身高
    private Integer height;

    // 身高条件：=、+、-
    private String heightOperator;

    // 分页参数
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
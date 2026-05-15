package com.example.demo.api.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改提议列表查询请求
 */
@Data
public class ChangeRequestQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    /** pending / approved / rejected,默认 pending */
    private String status = "pending";

    /** 当前访客 ID(可选);传入则在响应中回填 myVote */
    private String voterId;
}

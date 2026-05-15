package com.example.demo.api.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员审批请求(驳回时可带 reason)
 */
@Data
public class ReviewRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String reason;
}

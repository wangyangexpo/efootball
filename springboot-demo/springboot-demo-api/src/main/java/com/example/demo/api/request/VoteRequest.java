package com.example.demo.api.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 投票请求
 */
@Data
public class VoteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "voterId 不能为空")
    private String voterId;

    /** 1 = 赞同, -1 = 不赞同 */
    @NotNull(message = "voteType 不能为空")
    private Integer voteType;
}

package com.example.demo.api.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Player creation request
 */
@Data
public class PlayerCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "球员姓名不能为空")
    @Size(min = 1, max = 100, message = "球员姓名长度必须在1-100之间")
    private String name;

    @NotBlank(message = "球员位置不能为空")
    private String position;

    @NotBlank(message = "现役状态不能为空")
    private String status;

    private Integer number;

    private String club;

    private String league;

    private String country;

    private Integer height;

    private String foot;

    private String cardImage;

    // 安全验证密码
    @NotBlank(message = "密码不能为空")
    private String password;
}
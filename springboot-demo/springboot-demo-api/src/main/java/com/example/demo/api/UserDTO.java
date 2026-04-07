package com.example.demo.api;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User Data Transfer Object
 */
@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
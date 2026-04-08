package com.example.demo.api;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Player Data Transfer Object
 */
@Data
public class PlayerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String position;
    private String status;
    private Integer number;
    private String club;
    private String league;
    private String country;
    private Integer height;
    private String foot;
    private String cardImage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
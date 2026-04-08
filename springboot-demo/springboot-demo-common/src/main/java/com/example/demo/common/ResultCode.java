package com.example.demo.common;

import lombok.Getter;

/**
 * Response code enumeration
 */
@Getter
public enum ResultCode {

    SUCCESS("200", "Success"),
    FAIL("500", "Internal Server Error"),
    NOT_FOUND("404", "Resource Not Found"),
    BAD_REQUEST("400", "Bad Request"),
    USER_NOT_FOUND("1001", "User Not Found"),
    USER_ALREADY_EXISTS("1002", "User Already Exists"),
    PLAYER_NOT_FOUND("2001", "Player Not Found"),
    PLAYER_PASSWORD_INVALID("2002", "密码验证失败");

    private final String code;
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
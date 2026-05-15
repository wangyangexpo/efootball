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
    PLAYER_PASSWORD_INVALID("2002", "密码验证失败"),
    CHANGE_REQUEST_NOT_FOUND("3001", "修改提议不存在"),
    CHANGE_REQUEST_DUPLICATE_PENDING("3002", "您已对该球员有待审批的提议,请等待审批或修改后再提"),
    CHANGE_REQUEST_NO_DIFF("3003", "提议未包含任何字段改动"),
    CHANGE_REQUEST_NOT_PENDING("3004", "该提议已被处理,无法操作"),
    ADMIN_AUTH_REQUIRED("3010", "管理员鉴权失败");

    private final String code;
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
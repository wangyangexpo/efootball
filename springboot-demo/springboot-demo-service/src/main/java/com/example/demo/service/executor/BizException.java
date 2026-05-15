package com.example.demo.service.executor;

import lombok.Getter;

/**
 * 业务异常,executor 抛出后由 ServiceImpl 翻译为 Response.fail
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String code;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }
}

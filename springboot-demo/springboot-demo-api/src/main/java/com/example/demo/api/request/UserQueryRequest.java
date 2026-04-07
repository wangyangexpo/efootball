package com.example.demo.api.request;

import lombok.Data;
import java.io.Serializable;

/**
 * User query request with pagination
 */
@Data
public class UserQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private Integer status;

    // Pagination
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
package com.example.demo.api.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * User update request
 */
@Data
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(min = 2, max = 50, message = "Username length must be between 2 and 50")
    private String username;

    @Email(message = "Email format is invalid")
    private String email;

    private Integer status;
}
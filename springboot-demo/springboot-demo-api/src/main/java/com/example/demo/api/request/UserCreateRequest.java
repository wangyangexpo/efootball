package com.example.demo.api.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * User creation request
 */
@Data
public class UserCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 2, max = 50, message = "Username length must be between 2 and 50")
    private String username;

    @Email(message = "Email format is invalid")
    private String email;
}
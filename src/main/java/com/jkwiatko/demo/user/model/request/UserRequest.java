package com.jkwiatko.demo.user.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}

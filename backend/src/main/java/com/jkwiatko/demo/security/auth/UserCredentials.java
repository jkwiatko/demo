package com.jkwiatko.demo.security.auth;

import lombok.Data;

@Data
public class UserCredentials {
    private String email;
    private String password;
}


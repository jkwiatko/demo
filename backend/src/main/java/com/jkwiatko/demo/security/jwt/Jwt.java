package com.jkwiatko.demo.security.jwt;

import lombok.Data;

@Data
public class Jwt {
    private final String token;
    private final Long expirationTimestamp;
}
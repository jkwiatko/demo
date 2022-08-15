package com.jkwiatko.demo.security.jwt;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException exc) throws IOException {
        if (exc instanceof BadCredentialsException) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Wrong password or login");
        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not logged in, please log in");
        }
    }

}

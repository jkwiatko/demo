package com.jkwiatko.demo.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkwiatko.demo.security.auth.UserCredentials;
import com.jkwiatko.demo.security.jwt.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class JsonFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    public JsonFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            var user = objectMapper.readValue(request.getInputStream(), UserCredentials.class);
            var token =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(), user.getPassword());
            setDetails(request, token);
            return getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication) throws IOException {
        var principal = (UserDetails) authentication.getPrincipal();
        var token = jwtProvider.generateToken(principal);
        response.setHeader(CONTENT_TYPE, "application/json");
        objectMapper.writeValue(response.getWriter(), token);
    }
}

package com.jkwiatko.demo.security.filter;


import com.jkwiatko.demo.security.auth.UserDetailsServiceImpl;
import com.jkwiatko.demo.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor

public class JwtFilter extends OncePerRequestFilter {

    private static final Pattern TOKEN_REGEX = Pattern.compile("Bearer (?<token>.+)");

    private final JwtProvider tokenProvider;
    private final UserDetailsServiceImpl userPrincipalDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest httpServletRequest,
                                    @NonNull HttpServletResponse httpServletResponse,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            var jwt = getJwtFromRequest(httpServletRequest);
            if (tokenProvider.validateToken(jwt)) {
                String userEmail = tokenProvider.getUserIdFromJWT(jwt);
                UserDetails userDetails = userPrincipalDetailsService.loadUserByUsername(userEmail);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception exc) {
            logger.error("Could not set user authentication in security context", exc);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    public static String getJwtFromRequest(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION)).map(TOKEN_REGEX::matcher)
                .filter(Matcher::matches)
                .map(tokenMatcher -> tokenMatcher.group("token"))
                .orElse(null);
    }
}

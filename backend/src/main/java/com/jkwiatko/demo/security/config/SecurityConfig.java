package com.jkwiatko.demo.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkwiatko.demo.security.filter.JsonFilter;
import com.jkwiatko.demo.security.filter.JwtFilter;
import com.jkwiatko.demo.security.jwt.JwtEntryPoint;
import com.jkwiatko.demo.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    private final JwtFilter jwtFilter;
    private final JwtProvider jwtProvider;
    private final JwtEntryPoint jwtEntryPoint;
    private final ObjectMapper objectMapper;

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder,
                          UserDetailsService userDetailsService,
                          JwtFilter jwtFilter,
                          JwtProvider jwtProvider,
                          JwtEntryPoint jwtEntryPoint,
                          ObjectMapper objectMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
        this.jwtProvider = jwtProvider;
        this.jwtEntryPoint = jwtEntryPoint;
        this.objectMapper = objectMapper;
    }

    @Bean
    AuthenticationProvider authProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] publicUserEndpoints = {};

        http.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(publicUserEndpoints)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(createJsonFilter())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtEntryPoint);

        http.cors().configurationSource(corsConfigurationSource());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration());
        return source;
    }

    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(singletonList(allowedOrigin));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH", "OPTION"));
        corsConfiguration.setAllowedHeaders(List.of(AUTHORIZATION, CONTENT_TYPE));
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }

    private JsonFilter createJsonFilter() throws Exception {
        return new JsonFilter(objectMapper, authenticationManager(), jwtProvider);
    }
}


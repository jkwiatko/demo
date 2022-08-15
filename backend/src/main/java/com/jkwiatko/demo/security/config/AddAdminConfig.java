package com.jkwiatko.demo.security.config;

import com.jkwiatko.demo.user.entity.User;
import com.jkwiatko.demo.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static java.lang.String.format;

@Slf4j
@Configuration
@AllArgsConstructor
public class AddAdminConfig {

    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String TEST_ADMIN_LOG = "Test admin user present in DB to login use: email=%s, password=%s";

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner addTestUser() {
        return args -> {
            if (userRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
                var user = new User();
                user.setEmail(ADMIN_EMAIL);
                user.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
                userRepository.save(user);
            }
            log.info(format(TEST_ADMIN_LOG, ADMIN_EMAIL, ADMIN_PASSWORD));
        };
    }
}

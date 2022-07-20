package com.jkwiatko.demo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Slf4j
@Configuration
public class TestLibs {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    CommandLineRunner runningSomeTestCode() {
        return args -> {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + 6000);

            log.info("test jwt: {}",
                    Jwts.builder().setSubject("test").setIssuedAt(new Date()).setExpiration(expiryDate).signWith(SignatureAlgorithm.HS256, "test").compact());

        };
    }
}

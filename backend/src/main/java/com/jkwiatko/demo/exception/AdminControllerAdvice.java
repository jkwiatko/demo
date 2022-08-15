package com.jkwiatko.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static java.util.Optional.ofNullable;

@Slf4j
@ControllerAdvice
public class AdminControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleEntityIllegalState(IllegalArgumentException ex) {
        log.info(ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getLocalizedMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleEntityIllegalState(MethodArgumentNotValidException ex) {
        log.info(ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ofNullable(ex.getFieldError()).map(error -> error.getField() + " " + error.getDefaultMessage())
                        .orElse("Passed arguments didn't pass validation"));
    }
}

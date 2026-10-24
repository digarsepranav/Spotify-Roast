package com.spotify.demo.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {

//        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
//
//        for (FieldError errors : fieldErrors) {
//            String field = errors.getField();
//            String message = errors.getDefaultMessage();
//            System.out.println(field + " " + message);
//        }

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", errors));
    }
}

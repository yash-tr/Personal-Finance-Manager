package com.example.financemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.DateTimeException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        String message = "Invalid request format";
        if (ex.getMessage().contains("Cannot deserialize")) {
            message = "Invalid data format";
        }
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleDateTimeException(DateTimeException ex, WebRequest request) {
        return new ResponseEntity<>(Map.of("message", "Invalid date or time parameter"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(Map.of("message", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleResourceConflictException(ResourceConflictException ex, WebRequest request) {
        return new ResponseEntity<>(Map.of("message", ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return new ResponseEntity<>(Map.of("message", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex, WebRequest request) {
        return new ResponseEntity<>(Map.of("message", ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
        return new ResponseEntity<>(Map.of("message", "An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 
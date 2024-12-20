package ru.company.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        List<Map<String, String>> errorDetails = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            Map<String, String> fieldError = new HashMap<>();
            fieldError.put("field", error.getField());
            fieldError.put("defaultMessage", error.getDefaultMessage());
            errorDetails.add(fieldError);
        });

        errors.put("status", ex.getStatusCode());
        errors.put("errors", errorDetails);
        return errors;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFoundExceptions(NotFoundException ex) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("message", ex.getMessage());
        errors.put("status", ex.getStatusCode());
        return errors;
    }

    @ExceptionHandler(DuplicateUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleDuplicateUserExceptions(DuplicateUserException ex) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("message", ex.getMessage());
        errors.put("status", ex.getStatusCode());
        return errors;
    }
}

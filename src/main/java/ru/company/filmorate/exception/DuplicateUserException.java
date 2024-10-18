package ru.company.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class DuplicateUserException extends RuntimeException {

    String message;

    public DuplicateUserException(String msg) {
        super(msg);
        message = msg;
    }

    public HttpStatusCode getStatusCode() {
        return HttpStatus.CONFLICT;
    }
}

package ru.company.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class NotFoundException extends RuntimeException {

    String message;

    public NotFoundException(String msg) {
        super(msg);
        message = msg;
    }

    public HttpStatusCode getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}

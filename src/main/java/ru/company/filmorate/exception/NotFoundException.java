package ru.company.filmorate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
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

package com.hatchways.backEnd.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TagNotFound extends RuntimeException{
    public TagNotFound(String message) {
        super(message);
    }
}

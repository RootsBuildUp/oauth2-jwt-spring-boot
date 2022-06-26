package com.RootBuildUp.oauth2jwtspringboot.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) {
        super(msg);
    }
}

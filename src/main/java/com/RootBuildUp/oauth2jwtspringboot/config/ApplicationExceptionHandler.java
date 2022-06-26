package com.RootBuildUp.oauth2jwtspringboot.config;


import com.RootBuildUp.oauth2jwtspringboot.exception.ApiError;
import com.RootBuildUp.oauth2jwtspringboot.exception.BadRequestException;
import com.RootBuildUp.oauth2jwtspringboot.exception.CaseNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.io.FileNotFoundException;
import java.util.IllegalFormatException;
import java.util.UnknownFormatConversionException;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleStartCaseException(BadRequestException exception) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "This user can not start case");
        return new ResponseEntity<ApiError>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CaseNotFoundException.class)
    public ResponseEntity<ApiError> handleCaseNoFoundException(CaseNotFoundException exception) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "Case not found");
        return new ResponseEntity<ApiError>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ApiError> handleFileNotFoundException(FileNotFoundException fileNotFoundException) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, fileNotFoundException.getMessage(), fileNotFoundException.getMessage());
        return new ResponseEntity<ApiError>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleException(InvalidTokenException httpClientErrorException) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, httpClientErrorException.getMessage());
        return new ResponseEntity<ApiError>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiError> handleException(HttpClientErrorException httpClientErrorException) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, httpClientErrorException.getMessage());
        return new ResponseEntity<ApiError>(apiError, HttpStatus.UNAUTHORIZED);
    }


}

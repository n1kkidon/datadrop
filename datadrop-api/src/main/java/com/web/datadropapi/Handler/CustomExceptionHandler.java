package com.web.datadropapi.Handler;

import com.web.datadropapi.Handler.Exception.DuplicateDataException;
import com.web.datadropapi.Handler.Exception.SessionExpiredException;
import com.web.datadropapi.Handler.Exception.UserNotAuthenticatedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex, HttpServletRequest request){
        return buildErrorResponseEntity(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateDataException(DuplicateDataException ex, HttpServletRequest request){
        return buildErrorResponseEntity(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException ex, HttpServletRequest request){
        return buildErrorResponseEntity(ex, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InputMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInputMismatchException(InputMismatchException ex, HttpServletRequest request){
        return buildErrorResponseEntity(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request){
        return buildErrorResponseEntity(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotAuthenticatedException(UserNotAuthenticatedException ex, HttpServletRequest request){
        return buildErrorResponseEntity(ex, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ErrorResponse> handleSessionExpiredException(SessionExpiredException ex, HttpServletRequest request){
        return buildErrorResponseEntity(ex, request, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponseEntity(RuntimeException ex, HttpServletRequest request, HttpStatus status){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(status);
        errorResponse.setError(ex.getMessage());
        errorResponse.setPath(request.getServletPath());

        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }
}

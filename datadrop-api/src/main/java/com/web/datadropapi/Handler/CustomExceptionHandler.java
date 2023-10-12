package com.web.datadropapi.Handler;

import com.web.datadropapi.Handler.Exception.DuplicateDataException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorModel> handleNoSuchElementException(NoSuchElementException ex, HttpServletRequest request){
        ErrorModel errorModel = new ErrorModel();
        errorModel.setStatus(HttpStatus.NOT_FOUND);
        errorModel.setMessage(ex.getMessage());
        errorModel.setErrors(new ArrayList<>());
        return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ErrorModel> handleDuplicateDataException(DuplicateDataException ex, HttpServletRequest request){
        ErrorModel errorModel = ex.getErrorModel();
        errorModel.setStatus(HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorModel, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorModel> handleSecurityException(SecurityException ex, HttpServletRequest request){
        ErrorModel errorModel = new ErrorModel();
        errorModel.setStatus(HttpStatus.FORBIDDEN);
        errorModel.setMessage(ex.getMessage());
        errorModel.setErrors(new ArrayList<>());
        return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InputMismatchException.class)
    public ResponseEntity<ErrorModel> handleInputMismatchException(InputMismatchException ex, HttpServletRequest request){
        ErrorModel errorModel = new ErrorModel();
        errorModel.setStatus(HttpStatus.BAD_REQUEST);
        errorModel.setMessage(ex.getMessage());
        errorModel.setErrors(new ArrayList<>());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorModel> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request){
        ErrorModel errorModel = new ErrorModel();
        errorModel.setStatus(HttpStatus.BAD_REQUEST);
        errorModel.setMessage(ex.getMessage());
        errorModel.setErrors(new ArrayList<>());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }
}

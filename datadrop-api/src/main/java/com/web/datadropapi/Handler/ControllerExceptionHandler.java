package com.web.datadropapi.Handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setStatus(HttpStatus.PAYLOAD_TOO_LARGE);
        response.setError("File size exceeds the allowed limit. Please upload a smaller file.");
        response.setPath(request.getServletPath());

        return new ResponseEntity<>(response, response.getStatus());
    }
}

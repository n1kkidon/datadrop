package com.web.datadropapi.Handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorModel {
    private HttpStatus status;
    private ArrayList<java.lang.Error> errors = new ArrayList<>();
    private String message;

    public ErrorModel(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public void addError(java.lang.Error error){
        errors.add(error);
    }
}

package com.web.datadropapi.Handler.Exception;

import com.web.datadropapi.Handler.ErrorModel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserNotAuthenticatedException extends RuntimeException{
    ErrorModel errorModel;
    public UserNotAuthenticatedException(String message, ErrorModel errorModel) {
        super();
        this.errorModel = errorModel;
        errorModel.setMessage(message);
    }

    public UserNotAuthenticatedException(String message) {
        super();
        this.errorModel = new ErrorModel();
        this.errorModel.setMessage(message);
    }

    public UserNotAuthenticatedException(ErrorModel errorModel) {
        super();
        this.errorModel = errorModel;
    }
}

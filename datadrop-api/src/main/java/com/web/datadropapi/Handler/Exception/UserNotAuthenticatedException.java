package com.web.datadropapi.Handler.Exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserNotAuthenticatedException extends RuntimeException{
    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}

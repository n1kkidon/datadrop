package com.web.datadropapi.Handler.Exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionExpiredException extends RuntimeException{
    public SessionExpiredException(String message) {
        super(message);
    }
}

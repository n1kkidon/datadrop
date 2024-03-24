package com.web.datadropapi.Handler.Exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicateDataException extends RuntimeException{
    public DuplicateDataException(String message) {
        super(message);
    }
}

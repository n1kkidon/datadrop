package com.web.datadropapi.Handler.Exception;

import com.web.datadropapi.Handler.ErrorModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicateDataException extends RuntimeException{
    ErrorModel errorModel;
    public DuplicateDataException(String message, ErrorModel errorModel) {
        super();
        this.errorModel = errorModel;
        errorModel.setMessage(message);
    }

    public DuplicateDataException(String message) {
        super();
        this.errorModel = new ErrorModel();
        this.errorModel.setMessage(message);
    }

    public DuplicateDataException(ErrorModel errorModel) {
        super();
        this.errorModel = errorModel;
    }
}

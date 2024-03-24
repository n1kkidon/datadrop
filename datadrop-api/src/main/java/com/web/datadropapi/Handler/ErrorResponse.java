package com.web.datadropapi.Handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String timestamp;
    private HttpStatus status;
    private String error;
    private String path;

    public ErrorResponse()
    {
        setTimestamp(String.valueOf(new Date()));
    }
}

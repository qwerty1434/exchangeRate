package com.test.exchange.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {
    private int status;
    private String message;

    private ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse of(int status, String message){
        return new ErrorResponse(status,message);
    }

    public static ErrorResponse of(HttpStatus status, String message){
        return new ErrorResponse(status.value(),message);
    }

    public static ErrorResponse of(ErrorMessage errorMessage){
        return new ErrorResponse(errorMessage.getErrorCode(),errorMessage.getMessage());
    }
}

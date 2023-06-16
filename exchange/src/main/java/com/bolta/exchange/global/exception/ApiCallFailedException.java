package com.bolta.exchange.global.exception;

import com.bolta.exchange.apilayer.dto.ErrorResponse;

public class ApiCallFailedException extends RuntimeException {
    ErrorResponse errorResponse;

    public ApiCallFailedException(ErrorResponse errorResponse) {
        super(errorResponse.getInfo());
        this.errorResponse = errorResponse;
    }
}

package com.test.exchange.global.exception;

import com.test.exchange.apilayer.dto.ApiErrorResponse;

public class ApiServerDownException extends RuntimeException{
    ApiErrorResponse apiErrorResponse;

    public ApiServerDownException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse.getInfo());
        this.apiErrorResponse = apiErrorResponse;
    }
}

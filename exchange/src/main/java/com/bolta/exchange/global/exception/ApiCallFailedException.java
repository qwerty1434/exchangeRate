package com.bolta.exchange.global.exception;

import com.bolta.exchange.apilayer.dto.ApiErrorResponse;

public class ApiCallFailedException extends RuntimeException {
    ApiErrorResponse apiErrorResponse;

    public ApiCallFailedException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse.getInfo());
        this.apiErrorResponse = apiErrorResponse;
    }

}

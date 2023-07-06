package com.test.exchange.global.exception;

import com.test.exchange.apilayer.dto.ApiErrorResponse;

public class ApiCallFailedException extends RuntimeException {
    ApiErrorResponse apiErrorResponse;

    public ApiCallFailedException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse.getInfo());
        this.apiErrorResponse = apiErrorResponse;
    }

}

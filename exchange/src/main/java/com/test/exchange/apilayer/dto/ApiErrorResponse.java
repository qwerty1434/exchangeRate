package com.test.exchange.apilayer.dto;

import lombok.Builder;

public class ApiErrorResponse {
    private int code;
    private String type;
    private String info;

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    @Builder
    private ApiErrorResponse(int code, String type, String info) {
        this.code = code;
        this.type = type;
        this.info = info;
    }

    public static ApiErrorResponse of(int code, String type){
        return ApiErrorResponse.builder()
                .code(code)
                .type(type)
                .build();
    }

}

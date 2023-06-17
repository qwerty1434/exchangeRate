package com.bolta.exchange.global.exception;

public enum ErrorMessage {
    SAME_CURRENCY_ERROR("송금국가는 수취국가가 동일할 수 없습니다."),
    INVALID_SOURCE_VALUE_ERROR("올바른 송금국가가 아닙니다."),
    INVALID_TARGET_VALUE_ERROR("올바른 수취국가가 아닙니다."),
    INVALID_REMITTANCE_VALUE_ERROR("송금액이 올바르지 않습니다."),
    RATE_NOT_FOUND_ERROR("API서버에서 요청한 환율을 발견하지 못했습니다.");

    private final String errorMessage;

    ErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return errorMessage;
    }

}

package com.test.exchange.global.exception;

public enum ErrorMessage {
    SAME_CURRENCY_ERROR(400, "송금국가는 수취국가가 동일할 수 없습니다."),
    INVALID_SOURCE_VALUE_ERROR(400, "올바른 송금국가가 아닙니다."),
    INVALID_TARGET_VALUE_ERROR(400, "올바른 수취국가가 아닙니다."),
    INVALID_CURRENCY_VALUE_ERROR(400,"화폐 단위가 올바르지 않습니다."),
    INVALID_REMITTANCE_VALUE_ERROR(400, "송금액이 올바르지 않습니다."),
    RATE_NOT_FOUND_ERROR(400, "API서버에서 요청한 환율을 발견하지 못했습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부에 문제가 발생했습니다. 담당자에게 문의 부탁드립니다."),
    API_SERVER_ERROR(500,"API서버에 문제가 발생했습니다. 담당자에게 문의 부탁드립니다.");

    private final int errorCode;
    private final String errorMessage;

    ErrorMessage(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return errorMessage;
    }

}

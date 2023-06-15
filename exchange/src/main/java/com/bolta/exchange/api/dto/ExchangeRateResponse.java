package com.bolta.exchange.api.dto;

import com.bolta.exchange.exchange.domain.Currency;
import java.util.Map;


public class ExchangeRateResponse {
    private static final String API_CONNECTION_FAILED = "API로부터 데이터를 얻어오지 못했습니다.";
    private static final String NOT_FOUND_TARGET_DATA = "해당 데이터를 찾을 수 없습니다.";
    private boolean success;
    private Currency source;
    private Map<String,Double> quotes;
    private ErrorResponse error;


    public double getExchangeRate(Currency target){
        ValidateThatApiConnectionSucceeded();
        String key = source.toString()+target.toString();
        ValidateThatResponseContainsTargetData(key);
        return quotes.get(key);
    }

    // apiConnectionSucceed가 되야될거 같은데 그런건 밖에서 판단해야 될 거 같음
    public void ValidateThatApiConnectionSucceeded(){
        if(!success || quotes == null) throw new RuntimeException(API_CONNECTION_FAILED);
    }

    private void ValidateThatResponseContainsTargetData(String key) {
        if(!quotes.containsKey(key)) throw new RuntimeException(NOT_FOUND_TARGET_DATA);
    }

    public boolean isSuccess() {
        return success;
    }

    public Currency getSource() {
        return source;
    }

    public Map<String, Double> getQuotes() {
        return quotes;
    }

    public ErrorResponse getError() {
        return error;
    }
}

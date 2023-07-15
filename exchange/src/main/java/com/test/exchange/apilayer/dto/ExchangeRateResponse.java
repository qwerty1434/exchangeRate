package com.test.exchange.apilayer.dto;

import com.test.exchange.exchange.domain.Currency;
import com.test.exchange.global.exception.ApiCallFailedException;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.NoSuchElementException;

import static com.test.exchange.global.exception.ErrorMessage.RATE_NOT_FOUND_ERROR;

@NoArgsConstructor
public class ExchangeRateResponse {
    private boolean success;
    private int timestamp;
    private Currency source;
    private Map<String,Double> quotes;
    private ApiErrorResponse error;

    @Builder
    public ExchangeRateResponse(boolean success, int timestamp, Currency source, Map<String, Double> quotes, ApiErrorResponse error) {
        this.success = success;
        this.timestamp = timestamp;
        this.source = source;
        this.quotes = quotes;
        this.error = error;
    }

    public double getExchangeRate(Currency target){
        validateApiConnectionIsSuccess();
        String key = source.toString()+target.toString();
        validateKeyExistsInQuotes(key);
        return quotes.get(key);
    }

    private void validateApiConnectionIsSuccess(){
        if(!success || quotes.isEmpty()) throw
                new ApiCallFailedException(error);
    }

    private void validateKeyExistsInQuotes(String key) {
        if(!quotes.containsKey(key)) throw
                new NoSuchElementException(RATE_NOT_FOUND_ERROR.getMessage());
    }

    public boolean isSuccess() {
        return success;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Currency getSource() {
        return source;
    }

    public Map<String, Double> getQuotes() {
        return quotes;
    }

    public ApiErrorResponse getError() {
        return error;
    }

}

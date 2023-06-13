package com.bolta.exchange.api.dto;

import com.bolta.exchange.exchange.domain.Currency;
import java.util.Map;


public class ExternalApiResponse {
    private boolean success;
    private Currency source;
    private Map<String,Double> quotes;


    public double getExchangeRate(Currency target){
        String key = source.toString()+target.toString();
        return quotes.get(key);
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", source=" + source +
                ", quotes=" + quotes +
                '}';
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
}

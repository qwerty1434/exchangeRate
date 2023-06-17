package com.bolta.exchange.apilayer.domain;

import com.bolta.exchange.apilayer.dto.ExchangeRateResponse;
import com.bolta.exchange.exchange.domain.Currency;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.bolta.exchange.global.exception.ErrorMessage.*;

public class ExchangeRateClient {
    private final String requestUrl;

    public ExchangeRateClient(String baseUrl, String accessKey) {
        requestUrl = baseUrl + "?access_key=" + accessKey;
    }

    public ExchangeRateResponse getExchangeRate(Currency source, Currency target,
                                                List<Currency> allowedSources, List<Currency> allowedTargets){
        validateParams(source,target,allowedSources,allowedTargets);
        return WebClient.create()
                .get()
                .uri(makeUrl(source,target))
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .block();
    }

    private String makeUrl(Currency source, Currency target){
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(requestUrl);
        urlBuilder.append("&source=" + source);
        urlBuilder.append("&currencies=" + target);
        return urlBuilder.toString();
    }

    private void validateParams(Currency source, Currency target,
                                List<Currency> allowedSources, List<Currency> allowedTargets){
        validateSourceTargetDifferent(source,target);
        validateSourceCurrency(source,allowedSources);
        validateTargetCurrency(target,allowedTargets);
    }

    private void validateSourceTargetDifferent(Currency source, Currency target){
        if(source == target) throw
                new IllegalArgumentException(SAME_CURRENCY_ERROR.getMessage());
    }

    private void validateSourceCurrency(Currency source, List<Currency> allowedSources){
        if(!isAllowedValue(source,allowedSources)) throw
                new IllegalArgumentException(INVALID_SOURCE_VALUE_ERROR.getMessage());
    }

    private void validateTargetCurrency(Currency target, List<Currency> allowedTargets){
        if(!isAllowedValue(target,allowedTargets)) throw
                new IllegalArgumentException(INVALID_TARGET_VALUE_ERROR.getMessage());
    }

    private boolean isAllowedValue(Currency currency, List<Currency> allowedCurrencies){
        return allowedCurrencies.contains(currency);
    }

}

package com.test.exchange.apilayer.domain;

import com.test.exchange.apilayer.dto.ApiErrorResponse;
import com.test.exchange.apilayer.dto.ExchangeRateResponse;
import com.test.exchange.exchange.domain.Currency;
import com.test.exchange.global.exception.ApiServerDownException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.test.exchange.global.exception.ErrorMessage.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
public class ExchangeRateClient {
    private String requestUrl;

    private ExchangeRateClient(String baseUrl, String accessKey) {
        requestUrl = baseUrl + "?access_key=" + accessKey;
    }

    public static ExchangeRateClient of(String baseUrl, String accessKey){
        return new ExchangeRateClient(baseUrl,accessKey);
    }

    public ExchangeRateResponse getExchangeRate(Currency source, Currency target,
                                                List<Currency> allowedSources, List<Currency> allowedTargets) {
        assertParams(source, target, allowedSources, allowedTargets);
        return WebClient.create()
                .get()
                .uri(makeUrl(source, target))
                .retrieve()
                .onStatus(HttpStatus::isError, res -> res.bodyToMono(String.class)
                        .flatMap(error -> Mono.error(new ApiServerDownException(ApiErrorResponse.of(INTERNAL_SERVER_ERROR.value(), error)))))
                .bodyToMono(ExchangeRateResponse.class)
                .block();
    }

    private String makeUrl(Currency source, Currency target){
        return requestUrl
                + "&source=" + source
                + "&currencies=" + target;
    }

    private void assertParams(Currency source, Currency target,
                              List<Currency> allowedSources, List<Currency> allowedTargets){
        assertSourceTargetDifferent(source,target);
        assertSourceCurrency(source,allowedSources);
        assertTargetCurrency(target,allowedTargets);
    }

    private void assertSourceTargetDifferent(Currency source, Currency target){
        if(source == target) throw
                new IllegalArgumentException(SAME_CURRENCY_ERROR.getMessage());
    }

    private void assertSourceCurrency(Currency source, List<Currency> allowedSources){
        if(isNotAllowedValue(source,allowedSources)) throw
                new IllegalArgumentException(INVALID_SOURCE_VALUE_ERROR.getMessage());
    }

    private void assertTargetCurrency(Currency target, List<Currency> allowedTargets){
        if(isNotAllowedValue(target,allowedTargets)) throw
                new IllegalArgumentException(INVALID_TARGET_VALUE_ERROR.getMessage());
    }

    private boolean isNotAllowedValue(Currency currency, List<Currency> allowedCurrencies){
        return !allowedCurrencies.contains(currency);
    }

}

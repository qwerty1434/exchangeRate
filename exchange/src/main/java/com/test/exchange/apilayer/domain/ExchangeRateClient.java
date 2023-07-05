package com.test.exchange.apilayer.domain;

import com.test.exchange.apilayer.dto.ExchangeRateResponse;
import com.test.exchange.exchange.domain.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.test.exchange.global.exception.ErrorMessage.*;

@Slf4j
public class ExchangeRateClient {
    private static volatile ExchangeRateClient instance;
    private static final int CACHE_EXPIRATION_SECONDS = 60;
    private final String requestUrl;
    private final Map<String,ExchangeRateResponse> cache;

    private ExchangeRateClient(String baseUrl, String accessKey) {
        requestUrl = baseUrl + "?access_key=" + accessKey;
        cache = new HashMap<>();
    }

    public static synchronized ExchangeRateClient getInstance(String baseUrl, String accessKey) {
        if(instance == null){
            synchronized(ExchangeRateClient.class){
                if(instance == null){
                    instance = new ExchangeRateClient(baseUrl,accessKey);
                }
            }
        }
        return instance;
    }


    public ExchangeRateResponse getExchangeRate(Currency source, Currency target,
                                                List<Currency> allowedSources, List<Currency> allowedTargets) {
        assertParams(source, target, allowedSources, allowedTargets);

        String cacheKey = createCacheKey(source, target);
        if(isCacheValidate(cacheKey)) return cache.get(cacheKey);

        ExchangeRateResponse response = WebClient.create()
                .get()
                .uri(makeUrl(source, target))
                .retrieve()
                .onStatus(HttpStatus::isError, res -> res.bodyToMono(String.class)
                        .flatMap(error -> {
                            log.error(error);
                            return Mono.error(new RuntimeException(API_SERVER_ERROR.getMessage()));
                        }))
                .bodyToMono(ExchangeRateResponse.class)
                .block();
        cache.put(cacheKey,response);
        return response;
    }

    private String createCacheKey(Currency source, Currency target) {
        return source.toString() + target.toString();
    }

    private boolean isCacheValidate(String cacheKey){
        return cache.containsKey(cacheKey) &&
                System.currentTimeMillis() / 1000 - cache.get(cacheKey).getTimestamp() <= CACHE_EXPIRATION_SECONDS;
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

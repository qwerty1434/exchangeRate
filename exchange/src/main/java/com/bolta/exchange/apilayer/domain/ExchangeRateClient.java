package com.bolta.exchange.apilayer.domain;

import com.bolta.exchange.apilayer.dto.ExchangeRateResponse;
import com.bolta.exchange.exchange.domain.Currency;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.bolta.exchange.global.exception.ErrorMessage.*;

public class ExchangeRateClient {
    private static final ConcurrentHashMap<String,ExchangeRateClient> clientMap = new ConcurrentHashMap<>();
    private static final int CACHE_EXPIRATION_SECONDS = 60;
    private final String requestUrl;
    private final Map<String,ExchangeRateResponse> cache;

    private ExchangeRateClient(String baseUrl, String accessKey) {
        requestUrl = baseUrl + "?access_key=" + accessKey;
        cache = new HashMap<>();
    }

    public static synchronized ExchangeRateClient getInstance(String baseUrl, String accessKey) {
        String clientMapKey = createClientMapKey(baseUrl,accessKey);
        if(clientMap.containsKey(clientMapKey)) return clientMap.get(clientMapKey);
        ExchangeRateClient client = new ExchangeRateClient(baseUrl,accessKey);
        clientMap.put(clientMapKey,client);
        return client;
    }

    private static String createClientMapKey(String baseUrl, String accessKey){
        return baseUrl + accessKey;
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
                .bodyToMono(ExchangeRateResponse.class)
                .block();
        cache.put(cacheKey,response);
        return response;
    }

    private String createCacheKey(Currency source, Currency target) {
        return source.toString() + target.toString();
    }

    private boolean isCacheValidate(String cacheKey){
        if(cache.containsKey(cacheKey) &&
                System.currentTimeMillis()/1000 - cache.get(cacheKey).getTimestamp() <= CACHE_EXPIRATION_SECONDS) return true;
        return false;
    }

    private String makeUrl(Currency source, Currency target){
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(requestUrl);
        urlBuilder.append("&source=" + source);
        urlBuilder.append("&currencies=" + target);
        return urlBuilder.toString();
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
        if(!isAllowedValue(source,allowedSources)) throw
                new IllegalArgumentException(INVALID_SOURCE_VALUE_ERROR.getMessage());
    }

    private void assertTargetCurrency(Currency target, List<Currency> allowedTargets){
        if(!isAllowedValue(target,allowedTargets)) throw
                new IllegalArgumentException(INVALID_TARGET_VALUE_ERROR.getMessage());
    }

    private boolean isAllowedValue(Currency currency, List<Currency> allowedCurrencies){
        return allowedCurrencies.contains(currency);
    }

}

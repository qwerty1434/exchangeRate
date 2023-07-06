package com.test.exchange.apilayer.domain;

import com.test.exchange.apilayer.dto.ApiErrorResponse;
import com.test.exchange.apilayer.dto.ExchangeRateResponse;
import com.test.exchange.exchange.domain.Currency;
import com.test.exchange.global.exception.ApiServerDownException;
import com.test.exchange.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static com.test.exchange.global.exception.ErrorMessage.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateClient {
    private final RedisService redisService;

    private final int CACHE_EXPIRATION_SECONDS = 15;

    public ExchangeRateResponse getExchangeRate(String baseUrl, String accessKey,
                                                Currency source, Currency target,
                                                List<Currency> allowedSources, List<Currency> allowedTargets) {
        assertParams(source, target, allowedSources, allowedTargets);
        String cacheKey = makeCacheKey(baseUrl,accessKey);
        if(redisService.isExists(cacheKey)){
            return redisService.getValues(cacheKey);
        }else{
            ExchangeRateResponse result = WebClient.create()
                    .get()
                    .uri(makeUrl(baseUrl, accessKey))
                    .retrieve()
                    .onStatus(HttpStatus::isError, res -> res.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new ApiServerDownException(ApiErrorResponse.of(INTERNAL_SERVER_ERROR.value(), error)))))
                    .bodyToMono(ExchangeRateResponse.class)
                    .block();
            redisService.setValues(cacheKey,result,Duration.ofSeconds(CACHE_EXPIRATION_SECONDS));
            return result;
        }

    }

    private String makeCacheKey(String baseUrl, String accessKey){
        return baseUrl + accessKey;
    }


    private String makeUrl(String baseUrl, String accessKey, Currency source, Currency target){
        return baseUrl
                + "?access_key=" + accessKey
                + "&source=" + source
                + "&currencies=" + target;
    }
    private String makeUrl(String baseUrl, String accessKey){
        return baseUrl
                + "?access_key=" + accessKey;
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

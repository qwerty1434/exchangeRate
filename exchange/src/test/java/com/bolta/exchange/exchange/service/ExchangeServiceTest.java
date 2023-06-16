package com.bolta.exchange.exchange.service;

import com.bolta.exchange.apilayer.domain.ExchangeRateClient;
import com.bolta.exchange.apilayer.dto.ExchangeRateResponse;
import com.bolta.exchange.exchange.domain.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.bolta.exchange.exchange.domain.Currency.*;

@SpringBootTest
class ExchangeServiceTest {

    @Value("${api-layer.base-url}")
    private String baseUrl;
    @Value("${api-layer.access-key}")
    private String accessKey;
    @Value("$${api-layer.allowed-sources}")
    private List<Currency> allowedSources;
    @Value("$${api-layer.allowed-targets}")
    private List<Currency> allowedTargets;

    private static final Currency VALID_SOURCE = USD;
    private static final Currency VALID_TARGET = KRW;


    @DisplayName("미국 이외의 국가는 송금 불가능")
    @Test
    void func3(){
        Currency invalidSource = JPY;
        ExchangeRateClient exchangeRateClient = new ExchangeRateClient(baseUrl,accessKey);
        ExchangeRateResponse exchangeRateResponse = exchangeRateClient.getExchangeRate(invalidSource,VALID_TARGET,allowedSources,allowedTargets);
        exchangeRateResponse.getExchangeRate(VALID_TARGET);
    }

    @DisplayName("한국, 일본, 필리핀 이외의 국가는 수취 불가능")
    @Test
    void func4(){
        Currency invalidTarget = USD;
        ExchangeRateClient exchangeRateClient = new ExchangeRateClient(baseUrl,accessKey);
        ExchangeRateResponse exchangeRateResponse = exchangeRateClient.getExchangeRate(VALID_SOURCE,invalidTarget,allowedSources,allowedTargets);
        exchangeRateResponse.getExchangeRate(VALID_TARGET);

    }


}
package com.bolta.exchange.exchange.service;

import com.bolta.exchange.api.domain.ExchangeRateClient;
import com.bolta.exchange.api.dto.ExchangeRateResponse;
import com.bolta.exchange.exchange.domain.Currency;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Value;

import static com.bolta.exchange.exchange.domain.Currency.*;


class ExchangeServiceTest {

    @Value("${currency-layer.base-url}")
    private String baseUrl;
    @Value("${currency-layer.access-key}")
    private String accessKey;

    private static final Currency VALID_SOURCE = USD;
    private static final Currency VALID_TARGET = KRW;


    @DisplayName("미국 이외의 국가는 송금 불가능")
    void func3(){
        String invalidSource1 = "USDD"; // Pathvariable에서 failed to type convert 발생
        Currency invalidSource = JPY;
        ExchangeRateClient exchangeRateClient = new ExchangeRateClient(baseUrl,accessKey);
        ExchangeRateResponse exchangeRateResponse = exchangeRateClient.getExchangeRate(invalidSource,VALID_TARGET);

    }

    @DisplayName("한국, 일본, 필리핀 이외의 국가는 수취 불가능")
    void func4(){

    }


}
package com.bolta.exchange.api.domain;

import com.bolta.exchange.api.dto.ExchangeRateResponse;
import com.bolta.exchange.exchange.domain.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import static com.bolta.exchange.exchange.domain.Currency.KRW;
import static com.bolta.exchange.exchange.domain.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ExchangeRateClientTest {

    @Value("${currency-layer.base-url}")
    private String baseUrl;
    @Value("${currency-layer.access-key}")
    private String accessKey;

    private static final Currency VALID_SOURCE = USD;
    private static final Currency VALID_TARGET = KRW;


    @DisplayName("유효한 api와 url로 환율 가져오기")
    @ParameterizedTest
    @EnumSource(value = Currency.class, names = {"JPY","KRW","PHP"})
    void func1(Currency target){
        ExchangeRateClient exchangeRateClient = new ExchangeRateClient(baseUrl,accessKey);
        ExchangeRateResponse exchangeRateResponse = exchangeRateClient.getExchangeRate(VALID_SOURCE,target);
        assertThat(exchangeRateResponse.isSuccess()).isTrue();
    }

    @DisplayName("유효하지 않은 url주소를 사용하면 api로 환율 가져오기 실패")
    @Test
    void failGetApiDataWhenInvalidBaseUrl(){
        String invalidBaseUrl = "invalidUrl";
        ExchangeRateClient exchangeRateClient = new ExchangeRateClient(invalidBaseUrl,accessKey);
        assertThatThrownBy(() -> exchangeRateClient.getExchangeRate(VALID_SOURCE,VALID_TARGET))
                .isInstanceOf(WebClientRequestException.class);
    }

    @DisplayName("유효하지 않은 accessKey를 사용하면 api로 환율 가져오기 실패")
    @Test
    void failGetApiDataWhenInvalidAccessKey(){
        String invalidAccessKey = "invalidKey";
        ExchangeRateClient exchangeRateClient = new ExchangeRateClient(baseUrl,invalidAccessKey);
        ExchangeRateResponse exchangeRateResponse = exchangeRateClient.getExchangeRate(VALID_SOURCE,VALID_TARGET);
        exchangeRateResponse.getExchangeRate(VALID_TARGET);
    }

    @DisplayName("정상 성공")
    @Test
    void func5(){
        ExchangeRateClient exchangeRateClient = new ExchangeRateClient(baseUrl,accessKey);
        ExchangeRateResponse exchangeRateResponse = exchangeRateClient.getExchangeRate(VALID_SOURCE,VALID_TARGET);
        System.out.println("exchangeRateResponse = " + exchangeRateResponse);
    }

}
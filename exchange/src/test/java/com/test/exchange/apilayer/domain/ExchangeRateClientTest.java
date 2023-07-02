package com.test.exchange.apilayer.domain;

import com.test.exchange.apilayer.dto.ExchangeRateResponse;
import com.test.exchange.exchange.domain.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.ArrayList;
import java.util.List;

import static com.test.exchange.exchange.domain.Currency.*;
import static com.test.exchange.global.exception.ErrorMessage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ExchangeRateClientTest {
    @Value("${api-layer.base-url}")
    private String baseUrl;
    @Value("${api-layer.access-key}")
    private String accessKey;
    private static final List<Currency> allowedSources = List.of(USD);
    private static final List<Currency> allowedTargets = List.of(JPY,KRW,PHP);
    private static final Currency VALID_SOURCE = USD;
    private static final Currency VALID_TARGET = KRW;

    @DisplayName("유효하지 않은 url주소를 사용했을 때 webClientRequestException이 발생합니다.")
    @Test
    void testExchangeRateClientWithInvalidUrlAddress(){
        String invalidBaseUrl = "invalidUrl";
        ExchangeRateClient exchangeRateClient = ExchangeRateClient.getInstance(invalidBaseUrl,accessKey);

        assertThatThrownBy(() -> exchangeRateClient
                .getExchangeRate(VALID_SOURCE,VALID_TARGET,allowedSources,allowedTargets))
                .isInstanceOf(WebClientRequestException.class);
    }

    @DisplayName("유효하지 않은 accessKey를 사용했을 때 api서버로부터 실패 응답을 받게 됩니다.")
    @Test
    void testExchangeRateClientWithInvalidAccessKey(){
        String invalidAccessKey = "invalidKey";
        ExchangeRateClient exchangeRateClient = ExchangeRateClient.getInstance(baseUrl,invalidAccessKey);

        ExchangeRateResponse exchangeRateResponse = exchangeRateClient
                .getExchangeRate(VALID_SOURCE,VALID_TARGET,allowedSources,allowedTargets);

        assertThat(exchangeRateResponse.isSuccess()).isFalse();
        assertThat(exchangeRateResponse.getError().getType()).isEqualTo("invalid_access_key");
    }

    @DisplayName("source와 target이 동일하면 IllegalArgumentException이 발생합니다.")
    @Test
    void testExchangeRateClientWithSameSourceAndTarget(){
        Currency source = USD;
        Currency target = USD;
        ExchangeRateClient exchangeRateClient = ExchangeRateClient.getInstance(baseUrl,accessKey);

        assertThatThrownBy(() -> exchangeRateClient
                .getExchangeRate(source,target,allowedSources,allowedTargets))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(SAME_CURRENCY_ERROR.getMessage());
    }

    @DisplayName("유효하지 않은 source로 환율을 조회하면 IllegalArgumentException이 발생합니다.")
    @Test
    void testExchangeRateClientWithInvalidSource(){
        List<Currency> customAllowedSources = new ArrayList<>();
        customAllowedSources.add(USD);
        Currency excludedSource = JPY;
        ExchangeRateClient exchangeRateClient = ExchangeRateClient.getInstance(baseUrl,accessKey);

        assertThatThrownBy(() -> exchangeRateClient
                .getExchangeRate(excludedSource,VALID_TARGET,customAllowedSources,allowedTargets))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(INVALID_SOURCE_VALUE_ERROR.getMessage());
    }

    @DisplayName("유효하지 않은 target으로 환율을 조회하면 IllegalArgumentException이 발생합니다.")
    @Test
    void testExchangeRateClientWithInvalidTarget(){
        List<Currency> customAllowedTargets = new ArrayList<>();
        customAllowedTargets.add(JPY);
        customAllowedTargets.add(PHP);
        Currency excludedTarget = KRW;
        ExchangeRateClient exchangeRateClient = ExchangeRateClient.getInstance(baseUrl,accessKey);

        assertThatThrownBy(() -> exchangeRateClient
                .getExchangeRate(VALID_SOURCE,excludedTarget,allowedSources,customAllowedTargets))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(INVALID_TARGET_VALUE_ERROR.getMessage());
    }

    @DisplayName("유효한 값들로 환율을 조회하면 요청이 성공합니다.")
    @Test
    void testExchangeRateClientWithValidVariables(){
        ExchangeRateClient exchangeRateClient = ExchangeRateClient.getInstance(baseUrl,accessKey);

        ExchangeRateResponse exchangeRateResponse = exchangeRateClient
                .getExchangeRate(VALID_SOURCE,VALID_TARGET,allowedSources,allowedTargets);

        assertThat(exchangeRateResponse.isSuccess()).isTrue();
        assertThat(exchangeRateResponse.getExchangeRate(VALID_TARGET)).isGreaterThan(0);
    }

}
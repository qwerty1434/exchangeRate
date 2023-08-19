package com.test.exchange.exchange.service;

import com.test.exchange.apilayer.domain.ExchangeRateClient;
import com.test.exchange.apilayer.dto.ExchangeRateResponse;
import com.test.exchange.exchange.domain.Currency;
import com.test.exchange.exchange.dto.ExchangeMoneyResponse;
import com.test.exchange.exchange.repository.ExchangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;

import static com.test.exchange.exchange.domain.Currency.KRW;
import static com.test.exchange.exchange.domain.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {
    private static final Currency VALID_SOURCE = USD;
    private static final Currency VALID_TARGET = KRW;
    @MockBean
    ExchangeRateClient exchangeRateClient;
    @Autowired
    ExchangeRepository exchangeRepository;
    @Autowired
    ExchangeRepoClient exchangeRepoClient;
    ExchangeService exchangeService;

    @BeforeEach
    public void beforeEach(){
        exchangeService = new ExchangeService(exchangeRateClient, exchangeRepoClient);
    }

    @DisplayName("수취국가와 송금국가를 입력해 원하는 환율을 조회합니다.")
    @Test
    public void getExchangeRate(){
        double expectedValue = 1300;
        Map<String,Double> quotes = Map.of("USDKRW",expectedValue);
        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .success(true)
                .timestamp(0)
                .source(VALID_SOURCE)
                .quotes(quotes)
                .error(null)
                .build();

        BDDMockito.given(exchangeRateClient.getExchangeRate(any(),any(),any(),any(),any(),any()))
                .willReturn(response);

        double exchangeRate = exchangeService.getExchangeRate(VALID_SOURCE, VALID_TARGET);

        assertThat(exchangeRate).isEqualTo(expectedValue);
    }

    @DisplayName("환전 시 받게될 금액을 확인할 수 있습니다.")
    @Test
    public void getCalculatedRemittance(){
        double exchangeRate = 1300;
        Map<String,Double> quotes = Map.of("USDKRW",exchangeRate);
        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .success(true)
                .timestamp(0)
                .source(VALID_SOURCE)
                .quotes(quotes)
                .error(null)
                .build();
        double givenRemittance = 1000;

        BDDMockito.given(exchangeRateClient.getExchangeRate(any(),any(),any(),any(),any(),any()))
                .willReturn(response);

        double calculatedRemittance = exchangeService.getCalculatedRemittance(VALID_SOURCE, VALID_TARGET, givenRemittance);

        assertThat(calculatedRemittance).isEqualTo(exchangeRate * givenRemittance);
    }


    @DisplayName("환전을 하면 Target Currency로 변환된 돈을 반환합니다.")
    @Test
    public void exchangeMoney(){
        double exchangeRate = 1300;
        Map<String,Double> quotes = Map.of("USDKRW",exchangeRate);
        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .success(true)
                .timestamp(0)
                .source(VALID_SOURCE)
                .quotes(quotes)
                .error(null)
                .build();
        double givenRemittance = 1000;

        BDDMockito.given(exchangeRateClient.getExchangeRate(any(),any(),any(),any(),any(),any()))
                .willReturn(response);

        ExchangeMoneyResponse exchangeMoneyResponse =
                exchangeService.exchangeMoney(VALID_SOURCE, VALID_TARGET, givenRemittance);

        assertThat(VALID_TARGET).isEqualTo(exchangeMoneyResponse.getCurrency());
        assertThat(exchangeMoneyResponse.getRemittance()).isEqualTo(exchangeRate * givenRemittance);
    }

}
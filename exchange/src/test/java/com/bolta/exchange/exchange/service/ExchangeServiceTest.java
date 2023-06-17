package com.bolta.exchange.exchange.service;

import com.bolta.exchange.apilayer.domain.ExchangeRateClient;
import com.bolta.exchange.apilayer.dto.ExchangeRateResponse;
import com.bolta.exchange.exchange.domain.Currency;
import com.bolta.exchange.exchange.domain.Exchange;
import com.bolta.exchange.exchange.domain.Remittance;
import com.bolta.exchange.exchange.dto.ExchangeMoneyResponse;
import com.bolta.exchange.exchange.repository.ExchangeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.bolta.exchange.exchange.domain.Currency.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExchangeServiceTest {
    private static final Currency VALID_SOURCE = USD;
    private static final Currency VALID_TARGET = KRW;

    @Autowired
    ExchangeService exchangeService;
    @Autowired
    ExchangeRepository exchangeRepository;


    @DisplayName("Exchange엔티티가 제대로 저장되는지 확인")
    @Test
    public void testSaveExchange(){
        Remittance remittance = Remittance.from(1_000);
        double exchangeRate = 1.5;
        Exchange exchange = exchangeService.saveExchange(VALID_SOURCE, VALID_TARGET, remittance, exchangeRate);

        long exchangeId = exchange.getId();
        Exchange savedExchange = exchangeRepository.findById(exchangeId).orElse(null);

        assertThat(savedExchange).isNotNull();
    }

    @DisplayName("exchangeMoney가 Currency를 제대로 바꾸는지 확인")
    @Test
    public void testExchangeMoneyCurrency(){
        double givenRemittance = 1_000;
        ExchangeMoneyResponse exchangeMoneyResponse =
                exchangeService.exchangeMoney(VALID_SOURCE, VALID_TARGET, givenRemittance);
        assertThat(VALID_TARGET).isEqualTo(exchangeMoneyResponse.getCurrency());
    }
}
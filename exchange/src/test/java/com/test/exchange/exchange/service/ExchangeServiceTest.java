package com.test.exchange.exchange.service;

import com.test.exchange.exchange.domain.Currency;
import com.test.exchange.exchange.domain.Exchange;
import com.test.exchange.exchange.domain.Remittance;
import com.test.exchange.exchange.dto.ExchangeMoneyResponse;
import com.test.exchange.exchange.repository.ExchangeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.test.exchange.exchange.domain.Currency.KRW;
import static com.test.exchange.exchange.domain.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExchangeServiceTest {
    private static final Currency VALID_SOURCE = USD;
    private static final Currency VALID_TARGET = KRW;
    @Autowired
    ExchangeService exchangeService;
    @Autowired
    ExchangeRepository exchangeRepository;

    @DisplayName("Exchange엔티티를 저장합니다.")
    @Test
    public void testSaveExchange(){
        Remittance remittance = Remittance.from(1_000);
        double exchangeRate = 1.5;
        Exchange exchange = exchangeService.saveExchange(VALID_SOURCE, VALID_TARGET, remittance, exchangeRate);

        long exchangeId = exchange.getId();
        Exchange savedExchange = exchangeRepository.findById(exchangeId).orElse(null);

        assertThat(savedExchange).isNotNull();
    }

    @DisplayName("환전을 시도하면 Target Currency로 변환된 돈을 반환합니다.")
    @Test
    public void testExchangeMoneyCurrency(){
        double givenRemittance = 1_000;
        ExchangeMoneyResponse exchangeMoneyResponse =
                exchangeService.exchangeMoney(VALID_SOURCE, VALID_TARGET, givenRemittance);
        assertThat(VALID_TARGET).isEqualTo(exchangeMoneyResponse.getCurrency());
        assertThat(exchangeMoneyResponse.getRemittance()).isNotEqualTo(givenRemittance);
    }

}
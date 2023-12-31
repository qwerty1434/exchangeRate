package com.test.exchange.exchange.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.test.exchange.exchange.domain.Currency.KRW;
import static com.test.exchange.exchange.domain.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;

class ExchangeTest {
    private static final Currency VALID_SOURCE = USD;
    private static final Currency VALID_TARGET = KRW;

    @DisplayName("환율과 송금액을 이용해 환전 금액을 계산합니다.")
    @Test
    public void testCalculatingRemittanceWithExchangeEntity(){
        double remittanceValue = 1_000;
        Remittance remittance = Remittance.from(remittanceValue);
        double exchangeRate = 1.5;
        Exchange exchange = Exchange.of(VALID_SOURCE,VALID_TARGET,remittance,exchangeRate);
        assertThat(exchange.calculateRemittance()).isEqualTo(remittanceValue * exchangeRate);
    }

}
package com.test.exchange.exchange.service;

import com.test.exchange.exchange.domain.Currency;
import com.test.exchange.exchange.domain.Exchange;
import com.test.exchange.exchange.domain.Remittance;
import com.test.exchange.exchange.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExchangeRepoClient {
    private final ExchangeRepository exchangeRepository;

    @Transactional
    public Exchange saveExchange(Currency source, Currency target, Remittance remittance, double exchangeRate){
        Exchange exchange = Exchange.of(source,target,remittance,exchangeRate);
        return exchangeRepository.save(exchange);
    }
}

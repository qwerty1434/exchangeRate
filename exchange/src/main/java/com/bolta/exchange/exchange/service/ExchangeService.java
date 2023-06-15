package com.bolta.exchange.exchange.service;


import com.bolta.exchange.api.domain.ExchangeRateClient;
import com.bolta.exchange.api.dto.ExchangeRateResponse;
import com.bolta.exchange.exchange.domain.Currency;
import com.bolta.exchange.exchange.domain.Exchange;
import com.bolta.exchange.exchange.domain.Remittance;
import com.bolta.exchange.exchange.dto.ExchangeMoneyResponse;
import com.bolta.exchange.exchange.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final ExchangeRepository exchangeRepository;

    @Value("${currency-layer.base-url}")
    private String baseUrl;
    @Value("${currency-layer.access-key}")
    private String accessKey;

    @Transactional
    public double getExchangeRate(Currency source, Currency target){
        ExchangeRateClient exchangeRateClient = new ExchangeRateClient(baseUrl,accessKey);
        ExchangeRateResponse exchangeRateResponse = exchangeRateClient.getExchangeRate(source,target);
        return exchangeRateResponse.getExchangeRate(target);
    }

    @Transactional
    public double getCalculatedRemittance(Currency source, Currency target, double givenRemittance){
        Remittance remittance = Remittance.from(givenRemittance);
        double exchangeRate = getExchangeRate(source,target);
        return remittance.calculate(exchangeRate);
    }

    @Transactional
    public ExchangeMoneyResponse exchangeMoney(Currency source, Currency target, double givenRemittance){
        Remittance remittance = Remittance.from(givenRemittance);
        double exchangeRate = getExchangeRate(source,target);

        Exchange exchange = Exchange.of(source, target, remittance, exchangeRate);
        exchangeRepository.save(exchange);

        return new ExchangeMoneyResponse(exchange.getExchangedMoney(),target);
    }
}

package com.bolta.exchange.exchange.service;

import com.bolta.exchange.apilayer.domain.ExchangeRateClient;
import com.bolta.exchange.apilayer.dto.ExchangeRateResponse;
import com.bolta.exchange.exchange.domain.Currency;
import com.bolta.exchange.exchange.domain.Exchange;
import com.bolta.exchange.exchange.domain.Remittance;
import com.bolta.exchange.exchange.dto.ExchangeMoneyResponse;
import com.bolta.exchange.exchange.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final ExchangeRepository exchangeRepository;

    @Value("${api-layer.base-url}")
    private String baseUrl;
    @Value("${api-layer.access-key}")
    private String accessKey;
    @Value("$${api-layer.allowed-sources}")
    private List<Currency> allowedSources;
    @Value("$${api-layer.allowed-targets}")
    private List<Currency> allowedTargets;

    @Transactional
    public double getExchangeRate(Currency source, Currency target){
        ExchangeRateClient exchangeRateClient = ExchangeRateClient.getInstance(baseUrl,accessKey);
        ExchangeRateResponse exchangeRateResponse =
                exchangeRateClient.getExchangeRate(source,target,allowedSources,allowedTargets);
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

        Exchange exchange = saveExchange(source,target,remittance,exchangeRate);

        return new ExchangeMoneyResponse(exchange.calculateRemittance(),target);
    }

    @Transactional
    public Exchange saveExchange(Currency source, Currency target, Remittance remittance, double exchangeRate){
        Exchange exchange = Exchange.of(source,target,remittance,exchangeRate);
        return exchangeRepository.save(exchange);
    }

}

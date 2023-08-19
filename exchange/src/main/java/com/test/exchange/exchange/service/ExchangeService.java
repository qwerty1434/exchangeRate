package com.test.exchange.exchange.service;

import com.test.exchange.apilayer.domain.ExchangeRateClient;
import com.test.exchange.apilayer.dto.ExchangeRateResponse;
import com.test.exchange.exchange.domain.Currency;
import com.test.exchange.exchange.domain.Exchange;
import com.test.exchange.exchange.domain.Remittance;
import com.test.exchange.exchange.dto.ExchangeMoneyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final ExchangeRateClient exchangeRateClient;
    private final ExchangeRepoClient exchangeRepoClient;

    @Value("${api-layer.base-url}")
    private String baseUrl;
    @Value("${api-layer.access-key}")
    private String accessKey;
    @Value("$${api-layer.allowed-sources}")
    private List<Currency> allowedSources;
    @Value("$${api-layer.allowed-targets}")
    private List<Currency> allowedTargets;

    public double getExchangeRate(Currency source, Currency target){
        ExchangeRateResponse exchangeRateResponse =
                exchangeRateClient.getExchangeRate(baseUrl,accessKey,source,target,allowedSources,allowedTargets);
        double exchangeRate = exchangeRateResponse.getExchangeRate(target);
        return exchangeRate;
    }

    public double getCalculatedRemittance(Currency source, Currency target, double givenRemittance){
        Remittance remittance = Remittance.from(givenRemittance);
        double exchangeRate = getExchangeRate(source,target);
        return remittance.calculate(exchangeRate);
    }

    public ExchangeMoneyResponse exchangeMoney(Currency source, Currency target, double givenRemittance){
        Remittance remittance = Remittance.from(givenRemittance);
        double exchangeRate = getExchangeRate(source,target);
        Exchange exchange = exchangeRepoClient.saveExchange(source,target,remittance,exchangeRate);

        return new ExchangeMoneyResponse(exchange.calculateRemittance(),target);
    }

}

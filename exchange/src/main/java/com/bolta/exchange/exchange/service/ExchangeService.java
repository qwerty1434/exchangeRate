package com.bolta.exchange.exchange.service;


import com.bolta.exchange.api.domain.ExternalApiConnector;
import com.bolta.exchange.api.dto.ExternalApiResponse;
import com.bolta.exchange.exchange.domain.Currency;
import com.bolta.exchange.exchange.domain.Exchange;
import com.bolta.exchange.exchange.domain.Remittance;
import com.bolta.exchange.exchange.dto.ExchangeMoneyResponse;
import com.bolta.exchange.exchange.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final ExchangeRepository exchangeRepository;
    @Transactional
    public double getExchangeRate(Currency source, Currency target){
        ExternalApiResponse externalApiResponse = ExternalApiConnector.getExchangeRate(source,target);
        return externalApiResponse.getExchangeRate(target);
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

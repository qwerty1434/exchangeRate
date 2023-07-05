package com.test.exchange.exchange.service;

import com.test.exchange.apilayer.domain.ExchangeRateClient;
import com.test.exchange.apilayer.dto.ExchangeRateResponse;
import com.test.exchange.exchange.domain.Currency;
import com.test.exchange.exchange.domain.Exchange;
import com.test.exchange.exchange.domain.Remittance;
import com.test.exchange.exchange.dto.ExchangeMoneyResponse;
import com.test.exchange.exchange.repository.ExchangeRepository;
import com.test.exchange.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final ExchangeRepository exchangeRepository;
    private final RedisService redisService;

    private final int CACHE_VALIDATION_SECONDS = 60;

    @Value("${api-layer.base-url}")
    private String baseUrl;
    @Value("${api-layer.access-key}")
    private String accessKey;
    @Value("$${api-layer.allowed-sources}")
    private List<Currency> allowedSources;
    @Value("$${api-layer.allowed-targets}")
    private List<Currency> allowedTargets;

    public double getExchangeRate(Currency source, Currency target){
        String cacheKey = makeCacheKey(source,target);
        if(redisService.isExists(cacheKey)){
            return redisService.getValues(cacheKey);
        }else{
            ExchangeRateClient exchangeRateClient = ExchangeRateClient.of(baseUrl,accessKey);
            ExchangeRateResponse exchangeRateResponse =
                    exchangeRateClient.getExchangeRate(source,target,allowedSources,allowedTargets);
            double exchangeRate = exchangeRateResponse.getExchangeRate(target);
            redisService.setValues(cacheKey,exchangeRate,Duration.ofSeconds(CACHE_VALIDATION_SECONDS));
            return exchangeRate;
        }
    }

    public double getCalculatedRemittance(Currency source, Currency target, double givenRemittance){
        Remittance remittance = Remittance.from(givenRemittance);
        double exchangeRate = getExchangeRate(source,target);
        return remittance.calculate(exchangeRate);
    }

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

    private String makeCacheKey(Currency source, Currency target){
        return source+""+target;
    }

}

package com.bolta.exchange.api.domain;

import com.bolta.exchange.api.dto.ExchangeRateResponse;
import com.bolta.exchange.exchange.domain.Currency;
import org.springframework.web.reactive.function.client.WebClient;

public class ExchangeRateConnector {

    private final String requestUrl;


    public ExchangeRateConnector(String baseUrl, String accessKey) {
        requestUrl = baseUrl + "?access_key=" + accessKey;
    }

    public ExchangeRateResponse getExchangeRate(Currency source, Currency target){
        return WebClient.create()
                .get()
                .uri(makeUrl(source,target))
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .block();
    }


    private String makeUrl(Currency source, Currency target){
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(requestUrl);
        urlBuilder.append("&currencies=" + target);
        urlBuilder.append("&source=" + source);
        return urlBuilder.toString();
    }


}

package com.bolta.exchange.api.domain;


import com.bolta.exchange.api.dto.ExternalApiResponse;
import com.bolta.exchange.exchange.domain.Currency;
import org.springframework.web.reactive.function.client.WebClient;

public class ExternalApiConnector {
    private static final String BASE_URL = "http://apilayer.net/api/live";

    private static String API_KEY = "";

    public static ExternalApiResponse getExchangeRate(Currency source, Currency target){
        // WebClient
        return WebClient.create()
                .get()
                .uri(makeUrl(source, target))
                .retrieve()
                .bodyToMono(ExternalApiResponse.class)
                .block();
    }


    private static String makeUrl(Currency source, Currency target){
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BASE_URL);
        urlBuilder.append("?access_key=" + API_KEY);
        urlBuilder.append("&currencies=" + target);
        urlBuilder.append("&source=" + source);
        return urlBuilder.toString();
    }


}

package com.bolta.exchange.exchange.domain;

public enum Currency {
    USD("미국"),
    KRW("한국"),
    JPY("일본"),
    PHP("필리핀");

    private final String country;

    Currency(String country) {
        this.country = country;
    }

}

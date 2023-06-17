package com.bolta.exchange.exchange.dto;

import com.bolta.exchange.exchange.domain.Currency;

public class ExchangeMoneyResponse {
    double remittance;
    Currency currency;

    public ExchangeMoneyResponse(double remittance, Currency currency) {
        this.remittance = remittance;
        this.currency = currency;
    }

    public double getRemittance() {
        return remittance;
    }

    public Currency getCurrency() {
        return currency;
    }
}

package com.bolta.exchange.exchange.domain;

import static com.bolta.exchange.global.exception.ErrorMessage.INVALID_REMITTANCE_VALUE_ERROR;

public class Remittance {
    private double remittance;

    protected Remittance() {}

    private Remittance(double remittance) {
        assertRemittanceValueRange(remittance);
        this.remittance = remittance;
    }

    public static Remittance from(double remittance){
        return new Remittance(remittance);
    }

    public void assertRemittanceValueRange(double remittance){
        if(remittance < 0 || remittance > 10_000) throw
                new IllegalArgumentException(INVALID_REMITTANCE_VALUE_ERROR.getMessage());
    }

    public double getRemittance() {
        return remittance;
    }

    public double calculate(double exchangeRate){
        return remittance * exchangeRate;
    }

}

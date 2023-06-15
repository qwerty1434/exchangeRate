package com.bolta.exchange.exchange.domain;

public class Remittance {
    private static final String INVALID_REMITTANCE_VALUE = "송금액이 올바르지 않습니다.";
    private final double remittance;


    private Remittance(double remittance) {
        ValidateThatRemittanceValueIsWithinRange(remittance);
        this.remittance = remittance;
    }

    public static Remittance from(double remittance){
        return new Remittance(remittance);
    }

    public void ValidateThatRemittanceValueIsWithinRange(double remittance){
        if(remittance < 0 || remittance > 10_000) throw
                new IllegalArgumentException(INVALID_REMITTANCE_VALUE);
    }

    public double getRemittance() {
        return remittance;
    }

    public double calculate(double exchangeRate){
        return remittance * exchangeRate;
    }

}

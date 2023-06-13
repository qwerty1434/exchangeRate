package com.bolta.exchange.exchange.domain;

public class Remittance {
    private final double remittance;

    private Remittance(double remittance) {
        this.remittance = remittance;
    }

    public static Remittance from(double remittance){
        return new Remittance(remittance);
    }

    public double getRemittance() {
        return remittance;
    }

    public double calculate(double exchangeRate){
        return remittance * exchangeRate;
    }

}

package com.bolta.exchange.exchange.domain;

import javax.persistence.*;

@Entity
public class Exchange {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Currency source;
    private Currency target;
    @Embedded
    private Remittance remittance;
    double exchangeRate;

    protected Exchange(){}
    private Exchange(Currency source, Currency target, Remittance remittance, double exchangeRate) {
        this.source = source;
        this.target = target;
        this.remittance = remittance;
        this.exchangeRate = exchangeRate;
    }
    public static Exchange of(Currency source, Currency target, Remittance remittance, double exchangeRate){
        return new Exchange(source,target,remittance,exchangeRate);
    }

    public double calculateRemittance(){
        return remittance.getRemittance() * exchangeRate;
    }

    public Long getId() {
        return id;
    }
}

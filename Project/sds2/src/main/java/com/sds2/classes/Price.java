package com.sds2.classes;

import jakarta.persistence.Embeddable;

@Embeddable
public class Price {
    private double amount;
    private String currencyCode;

    public Price() {}

    public Price(double amount, String currencyCode) {
        this.amount = amount;
        this.currencyCode = currencyCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
}

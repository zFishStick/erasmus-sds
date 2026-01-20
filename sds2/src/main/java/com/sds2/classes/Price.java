package com.sds2.classes;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter @Setter
public class Price {
    private double amount;
    private String currencyCode;

    public Price() {}

    public Price(double amount, String currencyCode) {
        this.amount = amount;
        this.currencyCode = currencyCode;
    }
    
}

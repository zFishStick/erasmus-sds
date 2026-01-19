package com.sds2.classes.price;

public record PriceRange(Money startPrice, Money endPrice) {

    public record Money(
        String currencyCode,
        String units,
        int nanos
    ) {}
}


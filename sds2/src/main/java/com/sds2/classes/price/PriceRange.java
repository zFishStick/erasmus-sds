package com.sds2.classes.price;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceRange {
    private Money startPrice;
    private Money endPrice;
    
    @Setter @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Money {
        private String currencyCode;
        private String units;
        private int nanos;
    }
}


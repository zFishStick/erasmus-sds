package com.sds2.classes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PriceTest {

    @Test
    void gettersSettersAndConstructor() {
        Price p = new Price();
        assertEquals(0.0, p.getAmount());
        assertNull(p.getCurrencyCode());

        p.setAmount(99.99);
        p.setCurrencyCode("EUR");
        assertEquals(99.99, p.getAmount());
        assertEquals("EUR", p.getCurrencyCode());

        Price p2 = new Price(12.34, "USD");
        assertEquals(12.34, p2.getAmount());
        assertEquals("USD", p2.getCurrencyCode());
    }
}


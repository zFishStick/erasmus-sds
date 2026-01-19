package com.sds2.dto;

import org.junit.jupiter.api.Test;

import com.sds2.classes.Price;

public class POIDTOTest {
    @Test
    public void testPOIDTO(){
        String cityName = "hello";
        String name = "hello";
        String description = "hello";
        String type = "hello";
        Price price = new Price(1.2, "PLN");
        String pictures = "hello";
        String minimumDuration = "hello";
        String bookingLink = "hello";
        POIDTO poidto = new POIDTO(cityName, name, description, type, price, pictures, minimumDuration, bookingLink);
    }
}

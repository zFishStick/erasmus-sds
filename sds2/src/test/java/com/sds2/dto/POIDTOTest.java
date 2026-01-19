package com.sds2.dto;

import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

import com.sds2.classes.Price;
import com.sds2.classes.coordinates.GeoCode;

class POIDTOTest {

    @Test
    void testPOIDTO(){
        String cityName = "hello";
        String name = "hello";
        String description = "hello";
        String type = "hello";
        Price price = new Price(1.2, "PLN");
        String pictures = "hello";
        String minimumDuration = "hello";
        String bookingLink = "hello";
        GeoCode coordinates = new GeoCode(1.2, 1.2);

         POIDTO poiDTO = new POIDTO(cityName, name, description, type, price, pictures, minimumDuration, bookingLink, coordinates);

         assertAll(
            () -> poiDTO.cityName().equals(cityName),
            () -> poiDTO.name().equals(name),
            () -> poiDTO.description().equals(description),
            () -> poiDTO.type().equals(type),
            () -> poiDTO.price().equals(price),
            () -> poiDTO.pictures().equals(pictures),
            () -> poiDTO.minimumDuration().equals(minimumDuration),
            () -> poiDTO.bookingLink().equals(bookingLink),
            () -> poiDTO.coordinates().equals(coordinates)
         );
    }
}

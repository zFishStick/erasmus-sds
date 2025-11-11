package com.sds2.classes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class HotelTest {

    @Test
    void constructorAndGettersSetters() {
        com.sds2.classes.response.HotelResponse.Address addr = new com.sds2.classes.response.HotelResponse.Address();
        addr.setCountryCode("ES");
        addr.setLines(java.util.List.of("Gran Via 1"));

        com.sds2.classes.hotel.Hotel h = new com.sds2.classes.hotel.Hotel("H123", "Hotel Centro", "MAD", addr, new GeoCode(40.4, -3.7));

        assertEquals("H123", h.getHotelId());
        assertEquals("Hotel Centro", h.getName());
        assertEquals("MAD", h.getIataCode());
        assertEquals("ES", h.getAddress().getCountryCode());
        assertEquals(java.util.List.of("Gran Via 1"), h.getAddress().getLines());

        // setters
        h.setId(456L);
        h.setHotelId("H999");
        h.setName("Nuevo Nombre");
        h.setIataCode("BCN");
        h.setCoordinates(new GeoCode(41.4, 2.1));
        assertEquals(456L, h.getId());
        assertEquals("H999", h.getHotelId());
        assertEquals("Nuevo Nombre", h.getName());
        assertEquals("BCN", h.getIataCode());
        assertEquals(41.4, h.getCoordinates().getLatitude());
        assertEquals(2.1, h.getCoordinates().getLongitude());
    }
}

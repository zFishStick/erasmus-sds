package com.sds2.classes;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.sds2.classes.hotel.Hotel;
import com.sds2.classes.response.HotelResponse;

class HotelTest {

    @Test
    void constructorAndGettersSetters() {
        HotelResponse.Address addr = new HotelResponse.Address();
        addr.setCountryCode("ES");
        addr.setLines(List.of("Gran Via 1"));

        Hotel h = new Hotel("H123", "Hotel Centro", "MAD", addr, new GeoCode(40.4, -3.7));

        new Hotel();

        assertEquals("H123", h.getHotelId());
        assertEquals("Hotel Centro", h.getName());
        assertEquals("MAD", h.getIataCode());
        assertEquals("ES", h.getAddress().getCountryCode());
        assertEquals(List.of("Gran Via 1"), h.getAddress().getLines());

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

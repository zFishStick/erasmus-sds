package com.sds2.classes.request;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HotelRequestTest {

    @Test
    void constructorAndGettersSetters() {
        HotelRequest req = new HotelRequest("Paris", "FR", 48.85, 2.35, "2025-01-01", "2025-01-05");
        assertEquals("Paris", req.getDestination());
        assertEquals("FR", req.getCountryCode());
        assertEquals(48.85, req.getLatitude());
        assertEquals(2.35, req.getLongitude());
        assertEquals("2025-01-01", req.getCheckInDate());
        assertEquals("2025-01-05", req.getCheckOutDate());

        req.setDestination("Lyon");
        req.setCountryCode("ES");
        req.setLatitude(41.4);
        req.setLongitude(2.1);
        req.setCheckInDate("2025-02-01");
        req.setCheckOutDate("2025-02-04");
        assertEquals("Lyon", req.getDestination());
        assertEquals("ES", req.getCountryCode());
        assertEquals(41.4, req.getLatitude());
        assertEquals(2.1, req.getLongitude());
        assertEquals("2025-02-01", req.getCheckInDate());
        assertEquals("2025-02-04", req.getCheckOutDate());
    }
}

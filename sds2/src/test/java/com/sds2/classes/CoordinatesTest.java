package com.sds2.classes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.sds2.classes.coordinates.GeoCode;

class CoordinatesTest {

    @Test
    void gettersAndSettersWork() {
        GeoCode c = new GeoCode();
        assertEquals(0.0, c.getLatitude());
        assertEquals(0.0, c.getLongitude());

        c.setLatitude(12.34);
        c.setLongitude(-56.78);
        assertEquals(12.34, c.getLatitude());
        assertEquals(-56.78, c.getLongitude());
    }

    @Test
    void constructorSetsFields() {
        GeoCode c = new GeoCode(1.1, 2.2);
        assertEquals(1.1, c.getLatitude());
        assertEquals(2.2, c.getLongitude());
    }
}

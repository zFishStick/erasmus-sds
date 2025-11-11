package com.sds2.classes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GeoCodeTest {

    @Test
    void gettersSettersAndConstructor() {
        GeoCode g = new GeoCode();
        assertEquals(0.0, g.getLatitude());
        assertEquals(0.0, g.getLongitude());

        g.setLatitude(10.5);
        g.setLongitude(-3.2);
        assertEquals(10.5, g.getLatitude());
        assertEquals(-3.2, g.getLongitude());

        GeoCode g2 = new GeoCode(1.2, 3.4);
        assertEquals(1.2, g2.getLatitude());
        assertEquals(3.4, g2.getLongitude());
    }
}


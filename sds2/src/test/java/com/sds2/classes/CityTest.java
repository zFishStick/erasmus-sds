package com.sds2.classes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CityTest {

    @Test
    void constructorAndGetters() {
        City city = new City("Paris", "FR", 48.8566, 2.3522);
        assertEquals("Paris", city.getName());
        assertEquals("FR", city.getCountry());
        GeoCode coords = city.getCoordinates();
        assertEquals(48.8566, coords.getLatitude());
        assertEquals(2.3522, coords.getLongitude());
    }

    @Test
    void setCoordinatesAndToString() {
        City city = new City();
        city.setName("Lyon");
        city.setCountry("FR");
        city.setCoordinates(45.7640, 4.8357);

        GeoCode coords = city.getCoordinates();
        assertEquals(45.7640, coords.getLatitude());
        assertEquals(4.8357, coords.getLongitude());

        String s = city.toString();
        assertTrue(s.startsWith("City{name='Lyon', country='FR'"));
        assertTrue(s.contains("latitude="));
        assertTrue(s.contains("longitude="));
    }
}


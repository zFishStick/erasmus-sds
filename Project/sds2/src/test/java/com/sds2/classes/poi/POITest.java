package com.sds2.classes.poi;

import static org.junit.jupiter.api.Assertions.*;

import com.sds2.classes.GeoCode;
import com.sds2.classes.Price;
import org.junit.jupiter.api.Test;

class POITest {

    @Test
    void defaultConstructorInitializesFields() {
        new POI();

        POIInfo info = new POIInfo("Politechnic", "University",
         "A visit to the technological university", null, "1h", null);

        Price price = new Price(20.0, "PLN");

        GeoCode coords = new GeoCode(52.4064, 16.9252);

        POI poi2 = new POI(null, null, null, null, coords);
        Long id = 123L;
        poi2.setId(id);

        poi2.setCityName("Poznan");
        poi2.setCountryCode("PL");
        poi2.setInfo(info);
        poi2.setPrice(price);

        assertNotNull(poi2.getInfo());
        assertNotNull(poi2.getPrice());
        assertNotNull(poi2.getCoordinates());

        assertNotNull(poi2.getId());
    }

    @Test
    void gettersAndMutatorsDelegateCorrectly() {
        POIInfo info = new POIInfo("Park", "NATURE", "Nice green park", "pic1.jpg", "2h", "http://book");
        Price price = new Price(10.0, "EUR");
        GeoCode coords = new GeoCode(1.1, 2.2);

        POI poi = new POI("Paris", "FR", info, price, coords);

        assertEquals("Paris", poi.getCityName());
        assertEquals("FR", poi.getCountryCode());

        assertEquals("Park", poi.getName());
        poi.setName("Museum");
        assertEquals("Museum", poi.getName());

        assertEquals("Nice green park", poi.getDescription());
        poi.setDescription("Art museum");
        assertEquals("Art museum", poi.getDescription());

        assertEquals("NATURE", poi.getType());
        poi.setType("CULTURE");
        assertEquals("CULTURE", poi.getType());

        assertEquals(10.0, poi.getAmount());
        poi.setAmount(25.5);
        assertEquals(25.5, poi.getAmount());

        assertEquals("EUR", poi.getCurrencyCode());
        poi.setCurrencyCode("USD");
        assertEquals("USD", poi.getCurrencyCode());

        assertEquals(1.1, poi.getCoordinates().getLatitude());
        assertEquals(2.2, poi.getCoordinates().getLongitude());

        poi.setCoordinates(new GeoCode(3.3, 4.4));
        assertEquals(3.3, poi.getCoordinates().getLatitude());
        assertEquals(4.4, poi.getCoordinates().getLongitude());
    }
}


package com.sds2.classes.poi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class POIInfoTest {

    @Test
    void defaultConstructorInitializesFields() {
        new POIInfo();
        POIInfo poi = new POIInfo("Museum", "CULTURE", "A great museum", "museum.jpg", "3h", "http://bookmuseum");
        assertEquals("Museum", poi.getName());
        assertEquals("CULTURE", poi.getType());
        assertEquals("A great museum", poi.getDescription());
        assertEquals("museum.jpg", poi.getPictures());
        assertEquals("3h", poi.getMinimumDuration());
        assertEquals("http://bookmuseum", poi.getBookingLink());
    }

    @Test
    void gettersAndMutatorsWorkCorrectly() {
        POIInfo poiInfo = new POIInfo("Zoo", "NATURE", "City zoo", "zoo.jpg", "4h", "http://bookzoo");

        assertEquals("Zoo", poiInfo.getName());
        poiInfo.setName("Aquarium");
        assertEquals("Aquarium", poiInfo.getName());

        assertEquals("NATURE", poiInfo.getType());
        poiInfo.setType("EDUCATION");
        assertEquals("EDUCATION", poiInfo.getType());

        assertEquals("City zoo", poiInfo.getDescription());
        poiInfo.setDescription("Marine aquarium");
        assertEquals("Marine aquarium", poiInfo.getDescription());

        assertEquals("zoo.jpg", poiInfo.getPictures());
        poiInfo.setPictures("aquarium.jpg");
        assertEquals("aquarium.jpg", poiInfo.getPictures());

        assertEquals("4h", poiInfo.getMinimumDuration());
        poiInfo.setMinimumDuration("2h");
        assertEquals("2h", poiInfo.getMinimumDuration());

        assertEquals("http://bookzoo", poiInfo.getBookingLink());
        poiInfo.setBookingLink("http://bookaquarium");
        assertEquals("http://bookaquarium", poiInfo.getBookingLink());
    }
    
}

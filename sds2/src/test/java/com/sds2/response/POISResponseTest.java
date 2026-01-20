package com.sds2.response;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.sds2.classes.Price;
import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.response.POISResponse;
import com.sds2.classes.response.POISResponse.POIData;

class POISResponseTest {
    
    @Test
    void testPOISResponseCreation() {

        GeoCode geoCode = new GeoCode(40.7128, -74.0060);
        Price price = new Price(20.0, "USD");
        
        POIData poiData = new POIData(
            "Test POI",
            "A place of interest for testing.",
            "Museum",
            price,
            List.of("pic1.jpg", "pic2.jpg"),
            "2 hours",
            "http://bookinglink.com",
            geoCode
        );

        POISResponse poisResponse = new POISResponse(List.of(poiData));
        

        assertAll(
            () -> assertEquals(1, poisResponse.getData().size()),
            () -> assertEquals("Test POI", poiData.getName()),
            () -> assertEquals("A place of interest for testing.", poiData.getDescription()),
            () -> assertEquals("Museum", poiData.getType()),
            () -> assertEquals(price, poiData.getPrice()),
            () -> assertEquals(List.of("pic1.jpg", "pic2.jpg"), poiData.getPictures()),
            () -> assertEquals("2 hours", poiData.getMinimumDuration()),
            () -> assertEquals("http://bookinglink.com", poiData.getBookingLink()),
            () -> assertEquals(geoCode, poiData.getGeoCode())
        );

    }

}

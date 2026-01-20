package com.sds2.classes.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlacesRequestTest {

    @Test
    void testGettersAndSetters() {
        PlacesRequest req = new PlacesRequest();
        req.setDestination("Paris");
        req.setCountryCode("FR");

        assertEquals("Paris", req.getDestination());
        assertEquals("FR", req.getCountryCode());
    }
    
}

package com.sds2.service.chat;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CityContextTest {

    @Test
    void testCityContextCreation() {
        CityContext cityContext = new CityContext("Paris", "France", 48.8566, 2.3522);
        
        assertAll(
            () -> assertEquals("Paris", cityContext.destination()),
            () -> assertEquals("France", cityContext.country()),
            () -> assertEquals(48.8566, cityContext.latitude()),
            () -> assertEquals(2.3522, cityContext.longitude())
        );
    }
    
}

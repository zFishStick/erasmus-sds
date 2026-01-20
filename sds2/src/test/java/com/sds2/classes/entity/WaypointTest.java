package com.sds2.classes.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.sds2.classes.coordinates.Location;

class WaypointTest {
    
    @Test
    void testWaypointCreation() {
        Location location = new Location(40.7128, -74.0060);
        Waypoint waypoint = new Waypoint();
        waypoint.setId(1L);
        waypoint.setVia(true);
        waypoint.setName("Test Waypoint");
        waypoint.setLocation(location);
        waypoint.setAddress("123 Test St, Test City");
        waypoint.setDestination("Test Destination");
        waypoint.setCountry("Test Country");

        assertAll(
            () -> assertEquals(1L, waypoint.getId()),
            () -> assertTrue(waypoint.isVia()),
            () -> assertEquals("Test Waypoint", waypoint.getName()),
            () -> assertEquals(location, waypoint.getLocation()),
            () -> assertEquals("123 Test St, Test City", waypoint.getAddress()),
            () -> assertEquals("Test Destination", waypoint.getDestination()),
            () -> assertEquals("Test Country", waypoint.getCountry())
            
        );
    }

}

package com.sds2.classes.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.request.WaypointRequest;

class WaypointTest {
    
    @Test
    void testWaypointCreation() {
        Location location = new Location(40.7128, -74.0060);
        Waypoint waypoint = new Waypoint(1L,
                true,
                "Test Waypoint",
                location,
                "123 Test St, Test City",
                "Test Destination",
                "Test Country");

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

    @Test
    void testWaypointCreationFromRequest() {
            
        WaypointRequest waypointRequest = WaypointRequest.builder()
            .name("Test Waypoint")
            .address("123 Test St, Test City")
            .latitude(40.7128)
            .longitude(-74.0060)
            .destination("Test Destination")
            .country("Test Country")
            .userId(42L)
            .build();

        Waypoint waypoint = new Waypoint(waypointRequest);

        assertAll(
            () -> assertEquals("Test Waypoint", waypoint.getName()),
            () -> assertEquals(40.7128, waypoint.getLocation().getLatitude()),
            () -> assertEquals(-74.0060, waypoint.getLocation().getLongitude()),
            () -> assertEquals("123 Test St, Test City", waypoint.getAddress()),
            () -> assertEquals("Test Destination", waypoint.getDestination()),
            () -> assertEquals("Test Country", waypoint.getCountry()),
            () -> assertTrue(!waypoint.isVia())
        );
    }

}

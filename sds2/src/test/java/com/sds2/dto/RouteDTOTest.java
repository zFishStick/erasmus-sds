package com.sds2.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.sds2.classes.entity.Route;
import com.sds2.classes.entity.Waypoint;
import com.sds2.classes.enums.RouteTravelMode;

class RouteDTOTest {
    
    @Test
    void testRecordAccessorsEqualsAndHashCode() {
        List<String> intermediates = List.of("I1", "I2");
        RouteDTO dto = new RouteDTO("RID", "CityX", "CountryY", "Orig", "Dest", intermediates, "WALK");

        assertEquals("RID", dto.routeIdentifier());
        assertEquals("CityX", dto.city());
        assertEquals("CountryY", dto.country());
        assertEquals("Orig", dto.origin());
        assertEquals("Dest", dto.destination());
        assertEquals(intermediates, dto.intermediates());
        assertEquals("WALK", dto.travelMode());

        RouteDTO same = new RouteDTO("RID", "CityX", "CountryY", "Orig", "Dest", intermediates, "WALK");
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertTrue(dto.toString().contains("RID"));
    }

    @Test
    void testInequalityAndToStringContainsFields() {
        RouteDTO a = new RouteDTO("1", "C", "CO", "O", "D", java.util.List.of(), "T");
        RouteDTO b = new RouteDTO("2", "C", "CO", "O", "D", java.util.List.of(), "T");

        assertNotEquals(a, b);
        String s = a.toString();
        assertTrue(s.contains("routeIdentifier=1") || s.contains("1"));
        assertTrue(s.contains("C"));
        assertTrue(s.contains("T"));
    }

    @Test
    void testFromEntity() {
        Waypoint origin = new Waypoint();
        origin.setName("OriginName");
        Waypoint destination = new Waypoint();
        destination.setName("DestinationName");
        Waypoint wp1 = new Waypoint();
        wp1.setName("WP1");
        Waypoint wp2 = new Waypoint();
        wp2.setName("WP2");

        Route route = new Route();
        route.setRouteIdentifier("RID123");
        route.setCity("CityZ");
        route.setCountry("CountryW");
        route.setOrigin(origin);
        route.setDestination(destination);
        route.setIntermediates(List.of(wp1, wp2));
        route.setTravelMode(RouteTravelMode.DRIVING);

        RouteDTO dto = RouteDTO.fromEntity(route);

        assertEquals("RID123", dto.routeIdentifier());
        assertEquals("CityZ", dto.city());
        assertEquals("CountryW", dto.country());
        assertEquals("OriginName", dto.origin());
        assertEquals("DestinationName", dto.destination());
        assertEquals(List.of("WP1", "WP2"), dto.intermediates());
        assertEquals("DRIVING", dto.travelMode());
    }
}
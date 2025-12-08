package com.sds2.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.sds2.classes.CitySummary;
import com.sds2.classes.Places;
import com.sds2.classes.coordinates.Location;
import com.sds2.classes.request.RouteRequest;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.classes.routeclasses.Waypoint;
import com.sds2.service.PlaceService;
import com.sds2.service.RoutesService;
import com.sds2.service.WaypointService;

import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class RouteControllerTest {

    @Mock
    private RoutesService routesService;

    @Mock
    private WaypointService waypointService;

    @Mock
    private PlaceService placeService;

    @InjectMocks
    private RouteController routeController;

    @Test
    void addWaypoint_callsServicesAndReturnsOk() {
        WaypointRequest waypointRequest = new WaypointRequest(
            "Avenida Poznań",
            "Centrum handlowe, Stanisława Matyi 2, 61-586 Poznań, Poland",
            52.4003253,
            16.9135941,
            "Poznań",
            "Poland"
        );

        CitySummary citySummary = CitySummary.builder()
            .city("Poznań")
            .country("Poland")
            .build();

        Location location = Location.builder()
            .latitude(52.4003253)
            .longitude(16.9135941)
            .build();

        System.out.println(location.toString());

        Places place = Places.builder()
            .id(2L)
            .address("Centrum handlowe, Stanisława Matyi 2, 61-586 Poznań, Poland")
            .citySummary(citySummary)
            .location(location)
            .name("places/ChIJT8MYKzJbBEcRr1NmMv8AVxQ")
            .rating(4.2)
            .text("Avenida Poznań")
            .type("shopping_mall")
            .websiteUri("https://avenidapoznan.com/")
            .build();

        when(waypointService.addWaypoint(any(Waypoint.class)))
            .thenReturn("You have already added this waypoint");

        String response = routeController.addWaypoint(place.getId(), waypointRequest);

        assertEquals("You have already added this waypoint", response);

        verify(waypointService).addWaypoint(any(Waypoint.class));   
    }

    @Test
    void removeWaypoint_delegatesToService() {
        Long waypointId = 42L;

        routeController.removeWaypoint(waypointId);

        verify(waypointService).removeWaypoint(waypointId);
    }
}

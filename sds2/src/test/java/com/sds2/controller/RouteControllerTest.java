package com.sds2.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sds2.classes.coordinates.Location;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.service.PlaceService;
import com.sds2.service.RoutesService;
import com.sds2.service.WaypointService;

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
            "Poland",
            1L
        );

        Location location = Location.builder()
            .latitude(52.4003253)
            .longitude(16.9135941)
            .build();

        System.out.println(location.toString());

        when(waypointService.addWaypointForUser(any(WaypointRequest.class), waypointRequest.getUserId()))
            .thenReturn("You have already added this waypoint");

        Map<String, String> response = routeController.addWaypoint(waypointRequest);

        assertEquals("You have already added this waypoint", response.get("message"));

        verify(waypointService).addWaypointForUser(any(WaypointRequest.class), waypointRequest.getUserId());   
    }

    @Test
    void removeWaypoint_delegatesToService() {
        Long waypointId = 42L;

        routeController.removeWaypoint(waypointId);

        verify(waypointService).removeWaypoint(waypointId);
    }
}

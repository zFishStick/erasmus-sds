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

import com.sds2.classes.Places;
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
            "Avenida Poznan",
            "Stanis\u0142awa Matyi 2, 61-586 Poznan, Poland",
            52.4003253,
            16.9135941,
            "Poznan",
            "Poland"
        );
        Places place = Places.builder().id(1L).name("Avenida Poznan").text("Avenida Poznan").build();
        when(placeService.findPlaceByText(waypointRequest.getName())).thenReturn(place);

        String response = routeController.addWaypoint(place.getId(), waypointRequest);

        assertEquals("Waypoint added successfully", response);
        verify(placeService).findPlaceByText(waypointRequest.getName());
        verify(waypointService).addWaypoint(any(Waypoint.class));
    }

    @Test
    void removeWaypoint_delegatesToService() {
        Long waypointId = 42L;

        routeController.removeWaypoint(waypointId);

        verify(waypointService).removeWaypoint(waypointId);
    }

    @Test
    void createRoute_acceptsRouteRequestAndSetsSession() {
        RouteRequest routeRequest = new RouteRequest();
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        when(routesService.saveRoute(routeRequest));

        String result = routeController.createRoute("paris", routeRequest);
        
        assertNotNull(result);
        verify(routesService).saveRoute(routeRequest);
        verify(session).setAttribute("currentRoute", routeRequest);
    }
}

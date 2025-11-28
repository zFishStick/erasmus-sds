package com.sds2.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sds2.classes.Places;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.classes.routeclasses.Waypoint;
import com.sds2.service.PlaceService;
import com.sds2.service.WaypointService;

@SpringBootTest
class RouteControllerTest {

    @Autowired
    private RouteController routeController;
    @Autowired
    private WaypointService waypointService;
    @Autowired
    private PlaceService placeService;

    @Test
    void testAddWaypoint() {
        WaypointRequest waypointRequest = new WaypointRequest("Avenida Poznań",
         "Centrum handlowe, Stanisława Matyi 2, 61-586 Poznań, Poland", 52.4003253, 16.9135941);
        routeController.addWaypoint(waypointRequest);
    }

    @Test
    void testAddMultipleWaypoints() {
        WaypointRequest waypointRequest1 = new WaypointRequest("Avenida Poznań",
         "Centrum handlowe, Stanisława Matyi 2, 61-586 Poznań, Poland", 52.4003253, 16.9135941);
        routeController.addWaypoint(waypointRequest1);

        WaypointRequest waypointRequest2 = new WaypointRequest("Posnania",
         "Pleszewska 1, 61-136 Poznań, Poland", 52.3964356, 16.9555068);
        routeController.addWaypoint(waypointRequest2);
    }

    @Test
    void testFindWaypointByPlace() {
        Places place = placeService.findPlaceByText("Avenida Poznań");

        assertNotNull(place);

        Waypoint waypoint = waypointService.findWaypointByPlace(place);
        
        assertNotNull(waypoint);

    }

    @Test
    void testCreateRoute() {
        routeController.createRoute(1L, 2L);
    }
    
}

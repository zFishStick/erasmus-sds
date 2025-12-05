package com.sds2.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sds2.classes.Places;
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
    void testFindWaypointByPlace() {
        Places place = placeService.findPlaceByText("Avenida Poznań");

        assertNotNull(place);

        Waypoint waypoint = waypointService.findWaypointByPlace(place);
        
        assertNotNull(waypoint);

    }

    
}

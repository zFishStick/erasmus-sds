package com.sds2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sds2.classes.request.WaypointRequest;
import com.sds2.classes.routeclasses.Waypoint;
import com.sds2.repository.WaypointRepository;

@ExtendWith(MockitoExtension.class)

public class WayPointServiceTest {
    @Mock
    WaypointRepository waypointRepository;

    @InjectMocks
    private WaypointService waypointService;

    @Test
    void testAddWaypoint() {
        WaypointRequest waypointRequest = new WaypointRequest("name", "address", 1D, 1D, "destination", "country");
        Waypoint waypoint = new Waypoint(waypointRequest, 1L);
        Long id = waypoint.getId();
        waypointService.addWaypoint(waypointRequest);
        waypointService.addWaypoint(waypoint);
        waypointService.removeWaypoint(id);
        
    }
}

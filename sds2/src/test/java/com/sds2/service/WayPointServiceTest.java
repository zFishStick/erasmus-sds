package com.sds2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.entity.Waypoint;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.repository.WaypointRepository;

@ExtendWith(MockitoExtension.class)

class WayPointServiceTest {
    @Mock
    WaypointRepository waypointRepository;

    @InjectMocks
    private WaypointService waypointService;

    // @Test
    // void testAddWaypoint() {
    //     WaypointRequest waypointRequest = new WaypointRequest(
    //         "Avenida Poznan",
    //         "Centrum handlowe, Stanislawa Matyi 2, 61-586 Poznan, Poland",
    //         52.4003253,
    //         16.9135941,
    //         "Poznan",
    //         "Poland",
    //         1L
    //     );

    //     Waypoint waypoint = new Waypoint(
    //         1L,
    //         false,
    //         "Avenida Poznan",
    //         new Location(52.4003253, 16.9135941),
    //         "Centrum handlowe, Stanislawa Matyi 2, 61-586 Poznan, Poland",
    //         "Poznan",
    //         "Poland"
    //     );

    //     waypointService.addWaypointForUser(waypointRequest, waypointRequest.getUserId());
    //     waypointService.removeWaypoint(waypoint.getId());
    // }
}

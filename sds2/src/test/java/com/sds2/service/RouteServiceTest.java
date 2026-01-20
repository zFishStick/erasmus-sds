package com.sds2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sds2.classes.entity.Route;
import com.sds2.classes.entity.User;
import com.sds2.classes.entity.Waypoint;
import com.sds2.classes.enums.RouteTravelMode;
import com.sds2.classes.request.RouteRequest;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.repository.RoutesRepository;


@ExtendWith(MockitoExtension.class)
class RoutesServiceTest {

    @Mock
    private RoutesRepository routesRepository;

    @Mock
    private WaypointService waypointsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RoutesService routesService;

    @Test
    void getRouteByRouteIdentifier_returnsRouteWhenPresent() {
        Route r = Route.builder().routeIdentifier("r1").build();
        when(routesRepository.findByRouteIdentifier("r1")).thenReturn(r);

        Route result = routesService.getRouteByRouteIdentifier("r1");

        assertSame(r, result);
        verify(routesRepository).findByRouteIdentifier("r1");
    }

    @Test
    void getRouteByRouteIdentifier_returnsNullWhenMissing() {
        when(routesRepository.findByRouteIdentifier("missing")).thenReturn(null);

        Route result = routesService.getRouteByRouteIdentifier("missing");

        assertNull(result);
        verify(routesRepository).findByRouteIdentifier("missing");
    }

    @Test
    void deleteRouteByRouteIdentifier_deletesWhenExists() {
        Route r = Route.builder().routeIdentifier("toDelete").build();
        when(routesRepository.findByRouteIdentifier("toDelete")).thenReturn(r);

        String msg = routesService.deleteRouteByRouteIdentifier("toDelete");

        assertEquals("Route deleted successfully", msg);
        verify(routesRepository).findByRouteIdentifier("toDelete");
        verify(routesRepository).delete(r);
    }

    @Test
    void deleteRouteByRouteIdentifier_noExceptionWhenMissing() {
        when(routesRepository.findByRouteIdentifier("absent")).thenReturn(null);

        String msg = routesService.deleteRouteByRouteIdentifier("absent");

        assertEquals("Route deleted successfully", msg);
        verify(routesRepository).findByRouteIdentifier("absent");
        verify(routesRepository, never()).delete(any());
    }

    @Test
    void saveRoute_returnsErrorWhenIdentifierExists() {
        Route existing = Route.builder().routeIdentifier("dup").build();
        when(routesRepository.findByRouteIdentifier("dup")).thenReturn(existing);

        RouteRequest req = mock(RouteRequest.class);

        WaypointRequest origin = mock(WaypointRequest.class);
        when(req.getOrigin()).thenReturn(origin);
        when(origin.getLatitude()).thenReturn(0.0);
        when(origin.getLongitude()).thenReturn(0.0);

        WaypointRequest destination = mock(WaypointRequest.class);
        when(req.getDestination()).thenReturn(destination);
        when(destination.getLatitude()).thenReturn(1.0);
        when(destination.getLongitude()).thenReturn(1.0);

        when(req.getRouteIdentifier()).thenReturn("dup");


        String res = routesService.saveRoute(req, 1L);

        assertEquals("Route identifier already exists", res);
        verify(routesRepository).findByRouteIdentifier("dup");
        verify(routesRepository, never()).save(any());
    }

    @Test
    void saveRoute_savesAndRemovesTemporaryWaypoints_onSuccess_minimalFlow() {
        // Prepare request with identifier not existing
        RouteRequest req = mock(RouteRequest.class);
        when(req.getRouteIdentifier()).thenReturn("newId");

        // Provide origin/destination/intermediates with latitude/longitude via mocks
        WaypointRequest origin = mock(WaypointRequest.class);
        WaypointRequest destination = mock(WaypointRequest.class);
        when(req.getOrigin()).thenReturn(origin);
        when(req.getDestination()).thenReturn(destination);
        when(req.getIntermediates()).thenReturn(new WaypointRequest[0]);
        when(req.getCity()).thenReturn("C");
        when(req.getCountry()).thenReturn("X");
        when(req.getDepartureTime()).thenReturn("T");
        when(req.getTravelMode()).thenReturn(RouteTravelMode.DRIVING);

        when(routesRepository.findByRouteIdentifier("newId")).thenReturn(null);

        // WaypointService will report no existing waypoints so add will be called and then find returns a waypoint
        Waypoint wpOrigin = Waypoint.builder().id(10L).build();
        Waypoint wpDest = Waypoint.builder().id(20L).build();

        // Since we don't know exact types used inside RouteRequest for coordinates, instruct waypoint service behavior generically
        when(waypointsService.findWaypointByCoordinates(anyDouble(), anyDouble()))
            .thenReturn(null) // first calls: create then subsequent find returns actual waypoint
            .thenReturn(wpOrigin)
            .thenReturn(null)
            .thenReturn(wpDest);

        // User lookup and saved waypoints
        User u = User.builder()
            .id(1L)
            .savedWaypoints(new HashSet<>(List.of(wpOrigin, wpDest)))
            .build();
        when(userService.findById(1L)).thenReturn(u);

        // Call saveRoute
        String resp = routesService.saveRoute(req, 1L);

        assertEquals("Route saved successfully", resp);
        verify(routesRepository).save(any(Route.class));
        verify(userService).saveUser(any(User.class));
    }
}
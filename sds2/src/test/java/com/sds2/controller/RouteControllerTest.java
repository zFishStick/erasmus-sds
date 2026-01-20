package com.sds2.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.service.RoutesService;
import com.sds2.service.WaypointService;

@ExtendWith(MockitoExtension.class)
class RouteControllerTest {

    @Mock
    private RoutesService routesService;

    @Mock
    private WaypointService waypointService;

    @InjectMocks
    private RouteController routeController;

    @Test
    void addWaypoint_callsServicesAndReturnsOk() {
        WaypointRequest waypointRequest = new WaypointRequest(
            "Avenida Poznan",
            "Centrum handlowe, Stanislawa Matyi 2, 61-586 Poznan, Poland",
            52.4003253,
            16.9135941,
            "Poznan",
            "Poland",
            1L
        );

        when(waypointService.addWaypointForUser(any(WaypointRequest.class), eq(waypointRequest.getUserId())))
            .thenReturn("You have already added this waypoint");

        Map<String, String> response = routeController.addWaypoint(waypointRequest);

        assertEquals("You have already added this waypoint", response.get("message"));

        verify(waypointService).addWaypointForUser(any(WaypointRequest.class), eq(waypointRequest.getUserId()));   
    }

    @Test
    void removeWaypoint_delegatesToService() {
        Long waypointId = 42L;

        routeController.removeWaypoint(waypointId);

        verify(waypointService).removeWaypoint(waypointId);
    }

    @Test
    void saveRoute_returnsNotAuthenticated_whenNoUserInSession() {
        jakarta.servlet.http.HttpSession session = org.mockito.Mockito.mock(jakarta.servlet.http.HttpSession.class);
        when(session.getAttribute("user")).thenReturn(null);

        String result = routeController.saveRoute(null, session);

        assertEquals("User not authenticated", result);
        org.mockito.Mockito.verify(routesService, org.mockito.Mockito.never()).saveRoute(any(), any());
    }

    @Test
    void saveRoute_delegatesToRoutesService_whenUserAuthenticated() {
        jakarta.servlet.http.HttpSession session = org.mockito.Mockito.mock(jakarta.servlet.http.HttpSession.class);
        com.sds2.dto.UserDTO userDTO = org.mockito.Mockito.mock(com.sds2.dto.UserDTO.class);
        when(session.getAttribute("user")).thenReturn(userDTO);
        when(userDTO.id()).thenReturn(1L);

        com.sds2.classes.request.RouteRequest routeRequest = org.mockito.Mockito.mock(com.sds2.classes.request.RouteRequest.class);
        when(routesService.saveRoute(routeRequest, 1L)).thenReturn("route-saved");

        String result = routeController.saveRoute(routeRequest, session);

        assertEquals("route-saved", result);
        verify(routesService).saveRoute(routeRequest, 1L);
    }

    @Test
    void viewItinerary_populatesModelAndReturnsItineraryView() {
        org.springframework.ui.Model model = org.mockito.Mockito.mock(org.springframework.ui.Model.class);

        String country = "Poland";
        String destination = "Poznan";
        java.util.List<com.sds2.dto.WaypointDTO> waypoints = java.util.List.of(org.mockito.Mockito.mock(com.sds2.dto.WaypointDTO.class));
        when(waypointService.getWaypointsByDestinationAndCountry(destination, country)).thenReturn(waypoints);

        String view = routeController.viewItinerary(country, destination, model);

        assertEquals("itinerary", view);
        verify(waypointService).getWaypointsByDestinationAndCountry(destination, country);
        verify(model).addAttribute("city", destination);
        verify(model).addAttribute("country", country);
        verify(model).addAttribute("waypoints", waypoints);
    }

}
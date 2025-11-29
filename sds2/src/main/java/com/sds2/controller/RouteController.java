package com.sds2.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sds2.classes.Places;
import com.sds2.classes.request.RouteRequest;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.classes.routeclasses.Waypoint;

import com.sds2.service.PlaceService;
import com.sds2.service.RoutesService;
import com.sds2.service.WaypointService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/routes")
public class RouteController {
    private final RoutesService routesService;
    private final WaypointService waypointService;
    private final PlaceService placesService;

    @PostMapping("/waypoint/add")
    public ResponseEntity<Void> addWaypoint(WaypointRequest waypointRequest) {
        Places place = placesService.findPlaceByText(waypointRequest.getName());
        Waypoint waypoint = new Waypoint(waypointRequest, place);
        waypointService.addWaypoint(waypoint);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/waypoint/remove")
    public void removeWaypoint(Long id) {
        waypointService.removeWaypoint(id);
    }

    @PostMapping("/create")
    public void createRoute(RouteRequest routeRequest) {
        //routesService.createRoute(routeRequest);
    }

    @GetMapping("/itinerary")
    public String viewItinerary(Model model, HttpSession session) {
        List<Waypoint> waypoints = waypointService.getAllWaypoints();
        model.addAttribute("waypoints", waypoints);
        return "waypoints_page";
    }
    
}

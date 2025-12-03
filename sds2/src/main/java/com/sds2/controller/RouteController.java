package com.sds2.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @PostMapping("/create/{city}/{routeIdentifier}")
    @ResponseBody
    public RouteRequest createRoute(@PathVariable String city, @PathVariable String routeIdentifier, @RequestBody RouteRequest routeRequest, HttpSession session) throws JsonProcessingException {
        boolean response = routesService.saveRoute(routeRequest, routeIdentifier);
        
        if (!response) {
            return null;
        }

        session.setAttribute("currentRoute", routeRequest);
        
        return routeRequest;

    }


    @GetMapping("/itinerary/{destination}")
    public String viewItinerary(@PathVariable String destination, Model model) {

        List<Places> places = placesService.findPlacesByCitySummary_City(destination);
        if (places.isEmpty()) {
            return "error/404"; 
        }

        List<Waypoint> waypoints = waypointService.getWaypointsForPlaces(places);

        model.addAttribute("city", destination);
        model.addAttribute("waypoints", waypoints);

        return "waypoints_page";
    }

    
}

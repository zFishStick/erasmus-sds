package com.sds2.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sds2.classes.request.RouteRequest;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.classes.routeclasses.Waypoint;
import com.sds2.dto.WaypointDTO;
import com.sds2.service.PlaceService;
import com.sds2.service.RoutesService;
import com.sds2.service.WaypointService;

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

    @PostMapping("/waypoint/add/{id}")
    @ResponseBody
    public String addWaypoint(@PathVariable Long id, @RequestBody WaypointRequest waypointRequest) {
        Waypoint waypoint = new Waypoint(waypointRequest, id);
        return waypointService.addWaypoint(waypoint);
    }

    @PostMapping("/waypoint/remove/{id}")
    @ResponseBody
    public String removeWaypoint(@PathVariable Long id) {
        return waypointService.removeWaypoint(id);
    }

    @PostMapping("/save")
    public String postMethodName(@RequestBody RouteRequest routeRequest) {
        //TODO: process POST request
        
        return "Route saved successfully";
    }
    

    @PostMapping("/create/{city}")
    @ResponseBody
    public String createRoute(@PathVariable String city, RouteRequest routeRequest) {
        return routesService.saveRoute(routeRequest);     
    }

    @GetMapping("/itinerary/{country}/{destination}")
    public String viewItinerary(@PathVariable String country, @PathVariable String destination, Model model) {

        List<WaypointDTO> waypoints = waypointService.getWaypointsByDestinationAndCountry(destination, country);

        model.addAttribute("city", destination);
        model.addAttribute("country", country);
        model.addAttribute("waypoints", waypoints);

        return "itinerary";
    }

    
}

package com.sds2.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sds2.classes.request.RouteRequest;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.dto.UserDTO;
import com.sds2.dto.WaypointDTO;
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
    @ResponseBody
    public Map<String, String> addWaypoint(@RequestBody WaypointRequest waypointRequest) {
        String result = waypointService.addWaypointForUser(waypointRequest, waypointRequest.getUserId());
        return Map.of("message", result);
    }

    @PostMapping("/waypoint/remove/{id}")
    @ResponseBody
    public String removeWaypoint(@PathVariable Long id) {
        return waypointService.removeWaypoint(id);
    }

    @PostMapping("/save")
    @ResponseBody
    public String saveRoute(
        @RequestBody RouteRequest routeRequest,
        HttpSession session
    ) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");

        if (userDTO == null) {
            return "User not authenticated";
        }

        return routesService.saveRoute(routeRequest, userDTO.id());
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

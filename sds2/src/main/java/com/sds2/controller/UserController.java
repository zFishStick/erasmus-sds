package com.sds2.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sds2.classes.entity.Route;
import com.sds2.classes.request.UserRequest;
import com.sds2.classes.response.LoginResponse;
import com.sds2.dto.RouteDTO;
import com.sds2.dto.UserDTO;
import com.sds2.dto.WaypointDTO;
import com.sds2.service.RoutesService;
import com.sds2.service.UserService;
import com.sds2.service.WaypointService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final WaypointService waypointService;
    private final RoutesService routesService;

    public static final String COUNTRY_CODE = "countryCode";
    public static final String DESTINATION = "destination";
    public static final String REDIRECT = "redirect:/user/login";


    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(UserRequest userRequest, HttpSession session) {
        UserDTO user = userService.registerUser(userRequest);

        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/user";
        } else {
            return "redirect:/register?error=User already exists";
        }

    }
    
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String countryCode,
            HttpSession session,
            Model model) {

        LoginResponse result = userService.loginUser(email, password);

        if (!result.isSuccess()) {
            model.addAttribute("error", result.getMessage());
            model.addAttribute(DESTINATION, destination);
            model.addAttribute(COUNTRY_CODE, countryCode);
            return "login";
        }

        session.setAttribute("user", userService.getUserByEmail(email));

        if (destination != null && !destination.isBlank() &&
            countryCode != null && !countryCode.isBlank()) {
            return "redirect:/places/" + countryCode + "/" + destination;
        }


        return "redirect:/user";
    }



    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {

        String destination = (String) session.getAttribute(DESTINATION);
        String countryCode = (String) session.getAttribute(COUNTRY_CODE);

        model.addAttribute(DESTINATION, destination);
        model.addAttribute(COUNTRY_CODE, countryCode);

        return "login";
    }


    @GetMapping("/status")
    @ResponseBody
    public Map<String, Object> loginStatus(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        response.put("loggedIn", user != null);
        response.put("user", user);

        return response;
    }

    @GetMapping
    public String getUserFromSession(HttpSession session, Model model) {

        UserDTO user = (UserDTO) session.getAttribute("user");

        if (user == null) {
            return REDIRECT;
        }

        // Show all the itineraries for the user
        List<RouteDTO> routes = userService.getUserRoutes(user.id());
        model.addAttribute("itineraries", routes);

        // String city = (String) session.getAttribute("city");
        // String countryCode = (String) session.getAttribute(COUNTRY_CODE);

        // model.addAttribute("city", city);
        // model.addAttribute(COUNTRY_CODE, countryCode);

        model.addAttribute("user", user);
        return "user";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/itineraries")
    public String getUserItineraries(
            @RequestParam String city,
            @RequestParam(required = false) String countryCode,
            HttpSession session,
            Model model) {

        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user == null) {
            session.setAttribute(DESTINATION, city);
            session.setAttribute(COUNTRY_CODE, countryCode);
            return REDIRECT;
        }

        List<WaypointDTO> waypoints =
            waypointService.findByUserAndCity(
                user.id(),
                city,
                countryCode
            );

        model.addAttribute("city", city);
        model.addAttribute(COUNTRY_CODE, countryCode);
        model.addAttribute("waypoints", waypoints);
        model.addAttribute("user", user);

        return "itineraries";
    }

    @GetMapping("/itinerary/{routeIdentifier}")
    public String getItineraryByRouteIdentifier(
            @PathVariable String routeIdentifier,
            HttpSession session,
            Model model) {

        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user == null) {
            return REDIRECT;
        }

        Route route = routesService.getRouteByRouteIdentifier(routeIdentifier);

        System.out.println("Route found: " + route);

        List<WaypointDTO> waypoints = new ArrayList<>(
                route.getIntermediates()
                        .stream()
                        .map(WaypointDTO::fromEntity)
                        .toList()
        );


        System.out.println("Intermediates: " + waypoints);

        waypoints.add(0, WaypointDTO.fromEntity(route.getOrigin()));
        waypoints.add(WaypointDTO.fromEntity(route.getDestination()));

        model.addAttribute("routeIdentifier", routeIdentifier);
        model.addAttribute("waypoints", waypoints);
        model.addAttribute("user", user);

        return "itineraryDetails";
    }

    


    
    
}

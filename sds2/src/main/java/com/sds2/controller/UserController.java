package com.sds2.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sds2.classes.entity.Route;
import com.sds2.classes.entity.User;
import com.sds2.classes.request.POIRequest;
import com.sds2.classes.request.UserRequest;
import com.sds2.classes.response.LoginResponse;
import com.sds2.dto.RouteDTO;
import com.sds2.dto.UserDTO;
import com.sds2.dto.WaypointDTO;
import com.sds2.service.RoutesService;
import com.sds2.service.UserService;
import com.sds2.service.WaypointService;
import com.sds2.util.PasswordManager;

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

    public static final String REQUEST = "request";
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

    @PostMapping("/login/ajax")
    @ResponseBody
    public LoginResponse loginAjax(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String countryCode,
            HttpSession session) {

        User user = userService.findById(userService.getUserByEmail(email).id());

        if (user == null) {
            return new LoginResponse(false, LoginResponse.LoginStatus.USER_NOT_FOUND, null);
        }

        if (!PasswordManager.verifyPassword(password, user.getPassword())) {
            return new LoginResponse(false, LoginResponse.LoginStatus.INVALID_CREDENTIALS, null);
        }

        session.setAttribute("user", userService.getUserByEmail(email));

         if (destination != null && !destination.isBlank() &&
            countryCode != null && !countryCode.isBlank()) {
            session.setAttribute(DESTINATION, destination);
            session.setAttribute(COUNTRY_CODE, countryCode);
        }

        return new LoginResponse(true, LoginResponse.LoginStatus.SUCCESS, "/user");
    }

    @GetMapping("/login")
    public String loginPage(
        @RequestParam(required = false) String destination,
        @RequestParam(required = false) String countryCode,
        Model model
    ) {
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

        List<RouteDTO> routes = userService.getUserRoutes(user.id());
        
        POIRequest request = (POIRequest) session.getAttribute(REQUEST);

        if (request != null) {
            model.addAttribute(DESTINATION, request.getDestination());
            model.addAttribute(COUNTRY_CODE, request.getCountryCode());
        }

        
        Map<String, List<RouteDTO>> itinerariesByLocation =
        routes.stream()
                .collect(Collectors.groupingBy(
                        r -> r.city() + ", " + r.country(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        model.addAttribute("itinerariesByLocation", itinerariesByLocation);

        model.addAttribute("user", user);
        return "user";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/itineraries")
    public String loadUserItineraries(
        @RequestParam String destination,
        @RequestParam String country,
        HttpSession session
    ) {
        session.setAttribute(DESTINATION, destination);
        session.setAttribute(COUNTRY_CODE, country);

        return "redirect:/user/itineraries";
    }


    @GetMapping("/itineraries")
    public String getUserItineraries(
        HttpSession session,
        Model model
    ) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        POIRequest request = (POIRequest) session.getAttribute(REQUEST);

        if (request == null) {
            return "redirect:/";
        }

        if (user == null) {
            session.setAttribute(DESTINATION, request.getDestination());
            session.setAttribute(COUNTRY_CODE, request.getCountryCode());
            return REDIRECT;
        }

        List<WaypointDTO> waypoints =
            waypointService.findByUserAndCity(
                user.id(),
                request.getDestination(),
                request.getCountryCode()
            );

        model.addAttribute(REQUEST, request);
        model.addAttribute(DESTINATION, request.getDestination());
        model.addAttribute(COUNTRY_CODE, request.getCountryCode());
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

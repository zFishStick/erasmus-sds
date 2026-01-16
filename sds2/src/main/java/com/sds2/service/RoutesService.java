package com.sds2.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.entity.Route;
import com.sds2.classes.entity.User;
import com.sds2.classes.entity.Waypoint;
import com.sds2.classes.request.RouteRequest;
import com.sds2.repository.RoutesRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RoutesService {

    private final GoogleAuthService googleAuthService;
    private final RoutesRepository routesRepository;
    private final WebClient.Builder webClientBuilder;
    private final WaypointService waypointsService;
    private final UserService userService;

    public String saveRoute(RouteRequest req) {

        Double oriLat = req.getOrigin().getLatitude();
        Double oriLng = req.getOrigin().getLongitude();

        Double destLat = req.getDestination().getLatitude();
        Double destLng = req.getDestination().getLongitude();

        Route existing = routesRepository.findByRouteIdentifier(req.getRouteIdentifier());
        if (existing != null) {
            return "Route identifier already exists";
        }

        Waypoint origin = waypointsService.findWaypointByCoordinates(
            oriLat,
            oriLng
        );

        if (origin == null) {
            waypointsService.addWaypoint(req.getOrigin());
            origin = waypointsService.findWaypointByCoordinates(
                oriLat,
                oriLng
            );
        }

        Waypoint destination = waypointsService.findWaypointByCoordinates(
            destLat,
            destLng
        );

        List<Waypoint> intermediates = Arrays.stream(req.getIntermediates())
            .map(i -> {
                Waypoint wp = waypointsService.findWaypointByCoordinates(i.getLatitude(), i.getLongitude());
                if (wp == null) {
                    waypointsService.addWaypoint(i);
                    wp = waypointsService.findWaypointByCoordinates(i.getLatitude(), i.getLongitude());
                }
                return wp;
            })
            .toList();

        User user = userService.findById(req.getUserId());


        Route route = Route.builder()
                .routeIdentifier(req.getRouteIdentifier())
                .city(req.getCity())
                .country(req.getCountry())
                .departureTime(req.getDepartureTime())
                .origin(origin)
                .destination(destination)
                .intermediates(intermediates)
                .travelMode(req.getTravelMode())
                .user(user)
                .build();

        routesRepository.save(route);
        return "Route saved successfully";
    }

    public Route getRouteByRouteIdentifier(String routeIdentifier) {
        return routesRepository.findByRouteIdentifier(routeIdentifier);
    }

}


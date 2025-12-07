package com.sds2.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.request.RouteRequest;
import com.sds2.classes.routeclasses.Route;
import com.sds2.classes.routeclasses.Waypoint;
import com.sds2.repository.RoutesRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RoutesService {

    private final GoogleAuthService googleAuthService;
    private final RoutesRepository routesRepository;
    private final WebClient.Builder webClientBuilder;
    private final WaypointService waypointsService;

    public String saveRoute(RouteRequest req) {

        Double lat = req.getOrigin().getLatitude();
        Double lng = req.getOrigin().getLongitude();

        Route existing = routesRepository.findByRouteIdentifier(req.getRouteIdentifier());
        if (existing != null) {
            return "Route identifier already exists";
        }

        Waypoint origin = waypointsService.findWaypointByCoordinates(
            lat,
            lng
        );

        if (origin == null) {
            waypointsService.addWaypoint(req.getOrigin());
            origin = waypointsService.findWaypointByCoordinates(
                    lat,
                    lng
            );
        }

        Waypoint destination = waypointsService.findWaypointByCoordinates(
                lat,
                lng
        );

        List<Waypoint> intermediates = Arrays.stream(req.getIntermediates())
                .map(i -> waypointsService.findWaypointByCoordinates(
                        i.getLatitude(),
                        i.getLongitude()
                ))
                .toList();


        Route route = Route.builder()
                .routeIdentifier(req.getRouteIdentifier())
                .origin(origin)
                .destination(destination)
                .intermediates(intermediates)
                .travelMode(req.getTravelMode())
                .build();

        routesRepository.save(route);
        return "Route saved successfully";
    }

}


package com.sds2.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sds2.classes.entity.Route;
import com.sds2.classes.entity.User;
import com.sds2.classes.entity.Waypoint;
import com.sds2.classes.request.RouteRequest;
import com.sds2.repository.RoutesRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RoutesService {

    private final RoutesRepository routesRepository;
    private final WaypointService waypointsService;
    private final UserService userService;

    public String saveRoute(RouteRequest req, Long userId) {

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
            waypointsService.addWaypointForUser(req.getOrigin(), userId);
            origin = waypointsService.findWaypointByCoordinates(
                oriLat,
                oriLng
            );
        }

        Waypoint destination = waypointsService.findWaypointByCoordinates(
            destLat,
            destLng
        );

        if (destination == null) {
            waypointsService.addWaypointForUser(req.getDestination(), userId);
            destination = waypointsService.findWaypointByCoordinates(
                destLat,
                destLng
            );
        }

        List<Waypoint> intermediates = Arrays.stream(req.getIntermediates())
            .map(i -> {
                Waypoint wp = waypointsService.findWaypointByCoordinates(i.getLatitude(), i.getLongitude());
                if (wp == null) {
                    waypointsService.addWaypointForUser(i, userId);
                    wp = waypointsService.findWaypointByCoordinates(i.getLatitude(), i.getLongitude());
                }
                return wp;
            })
            .toList();

        User user = userService.findById(userId);

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

        removeUserSavedWaypoints(user, intermediates, origin, destination);

        return "Route saved successfully";
    }

    public Route getRouteByRouteIdentifier(String routeIdentifier) {
        return routesRepository.findByRouteIdentifier(routeIdentifier);
    }
    
    public String deleteRouteByRouteIdentifier(String routeIdentifier) {
        Route route = routesRepository.findByRouteIdentifier(routeIdentifier);
        if (route != null) {
            routesRepository.delete(route);
        }
        return "Route deleted successfully";
    }

    private void removeUserSavedWaypoints(User user, List<Waypoint> waypoints, Waypoint ori, Waypoint dest) {
        Set<Long> waypointIds = waypoints.stream()
            .filter(Objects::nonNull)
            .map(Waypoint::getId)
            .collect(Collectors.toSet());

        List<Waypoint> toRemove = user.getSavedWaypoints()
            .stream()
            .filter(wp -> waypointIds.contains(wp.getId()))
            .toList();

        user.getSavedWaypoints().remove(ori);
        user.getSavedWaypoints().remove(dest);
        user.getSavedWaypoints().removeAll(toRemove);
        userService.saveUser(user);
    }

}


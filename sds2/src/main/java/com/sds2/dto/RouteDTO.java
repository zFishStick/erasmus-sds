package com.sds2.dto;

import java.util.List;

import com.sds2.classes.entity.Route;
import com.sds2.classes.entity.Waypoint;

public record RouteDTO(
    String routeIdentifier,
    String city,
    String country,
    String origin,
    String destination,
    List<String> intermediates,
    String travelMode
) {
    public static RouteDTO fromEntity(Route route) {
        return new RouteDTO(
            route.getRouteIdentifier(),
            route.getCity(),
            route.getCountry(),
            route.getOrigin().getName(),
            route.getDestination().getName(),
            route.getIntermediates().stream()
                .map(Waypoint::getName)
                .toList(),
            route.getTravelMode().toString()
        );
    }
}


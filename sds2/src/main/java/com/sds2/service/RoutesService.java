package com.sds2.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private void addRoute(Route route) {
        if (route != null) {
            routesRepository.save(route);
        }
    }

    public boolean saveRoute(RouteRequest req, String routeIdentifier) {

        Route existing = routesRepository.findByRouteIdentifier(routeIdentifier);
        if (existing != null) {
            return false;
        }

        Waypoint origin = waypointsService.findWaypointByCoordinates(
                req.getOrigin().getLat(),
                req.getOrigin().getLng()
        );

        if (origin == null) {
            waypointsService.addWaypoint(req.getOrigin());
            origin = waypointsService.findWaypointByCoordinates(
                    req.getOrigin().getLat(),
                    req.getOrigin().getLng()
            );
        }

        Waypoint destination = waypointsService.findWaypointByCoordinates(
                req.getDestination().getLat(),
                req.getDestination().getLng()
        );

        List<Waypoint> intermediates = Arrays.stream(req.getIntermediates())
                .map(i -> waypointsService.findWaypointByCoordinates(
                        i.getLat(),
                        i.getLng()
                ))
                .collect(Collectors.toList());


        Route route = Route.builder()
                .routeIdentifier(routeIdentifier)
                .origin(origin)
                .destination(destination)
                .intermediates(intermediates)
                .travelMode(req.getTravelMode())
                // .distanceMeters(req.getDistanceMeters())
                // .departureTime(req.getDepartureTime())
                // .arrivalTime(req.getArrivalTime())
                // .encodedPolyline(req.getEncodedPolyline())
                .build();

        routesRepository.save(route);
        return true;
    }


    // public String buildComputeRoutesBody(RouteRequest req) throws JsonProcessingException {
    //     ObjectMapper mapper = new ObjectMapper();

    //     ObjectNode root = mapper.createObjectNode();

    //     ObjectNode originNode = root.putObject("origin")
    //                             .putObject("location")
    //                             .putObject("latLng");
    //     originNode.put("latitude", req.getOrigin().getLat());
    //     originNode.put("longitude", req.getOrigin().getLng());

    //     ObjectNode destNode = root.putObject("destination")
    //                             .putObject("location")
    //                             .putObject("latLng");
    //     destNode.put("latitude", req.getDestination().getLat());
    //     destNode.put("longitude", req.getDestination().getLng());

    //     ArrayNode intermediatesNode = root.putArray("intermediates");
    //     for (WaypointRequest wp : req.getIntermediates()) {
    //         ObjectNode wpNode = intermediatesNode.addObject();
    //         ObjectNode loc = wpNode.putObject("location").putObject("latLng");
    //         loc.put("latitude", wp.getLat());
    //         loc.put("longitude", wp.getLng());
    //     }

    //     root.put("travelMode", req.getTravelMode().name());
    //     root.put("routingPreference", "TRAFFIC_AWARE");
    //     root.put("departureTime", DataISOFormatter.formatToISO8601(req.getDepartureTime()));
    //     root.put("computeAlternativeRoutes", false);

    //     ObjectNode routeModifiers = root.putObject("routeModifiers");
    //     routeModifiers.put("avoidTolls", false);
    //     routeModifiers.put("avoidHighways", false);
    //     routeModifiers.put("avoidFerries", false);

    //     root.put("languageCode", "en-US");
    //     root.put("units", "METRIC");

    //     return mapper.writeValueAsString(root);
    // }

    // private RouteDTO mapToRouteDTO(RouteResponse response, RouteRequest req, String routeIdentifier) {

    //     Waypoint o = waypointsService.findWaypointByCoordinates(
    //         req.getOrigin().getLat(),
    //         req.getOrigin().getLng()
    //     );

    //     Waypoint d = waypointsService.findWaypointByCoordinates(
    //         req.getDestination().getLat(),
    //         req.getDestination().getLng()
    //     );

    //     Route route = Route.builder()
    //         .routeIdentifier(routeIdentifier)
    //         .travelMode(req.getTravelMode())
    //         .origin(o)
    //         .destination(d)
    //         .distanceMeters(response.getRoutes().get(0).getLegs()[0].getDistanceMeters())
    //         .duration(response.getRoutes().get(0).getLegs()[0].getDuration())
    //         .encodedPolyline(response.getRoutes().get(0).getLegs()[0].getPolyline().getEncodedPolyline())
    //         .build();

    //     addRoute(route);

    //     RouteLeg leg = response.getRoutes().get(0).getLegs()[0];
    //     return new RouteDTO(
    //         leg.getDistanceMeters(),
    //         leg.getDuration(),
    //         req.getOrigin().getName(),
    //         req.getDestination().getName(),
    //         leg.getPolyline().getEncodedPolyline()
    //     );
    // }

    // public RouteDTO computeRoute(RouteRequest req, String routeIdentifier) throws JsonProcessingException {

    // Route route = routesRepository.findByRouteIdentifier(routeIdentifier);
    // if (route != null) {
    //     return new RouteDTO(
    //         route.getDistanceMeters(),
    //         route.getDuration(),
    //         req.getOrigin().getName(),
    //         req.getDestination().getName(),
    //         route.getEncodedPolyline()
    //     );
    // }

    // String url = "https://routes.googleapis.com/directions/v2:computeRoutes";

    // String body = buildComputeRoutesBody(req);

    // Logger logger = Logger.getLogger(RoutesService.class.getName());
    // logger.info("Request Body: " + body);

    // RouteResponse response = webClientBuilder.build()
    //     .post()
    //     .uri(url)
    //     .header(GoogleBodyEnum.CONTENTTYPE.getValue(), GoogleBodyEnum.APPLICATIONJSON.getValue())
    //     .header(GoogleBodyEnum.X_GOOG_API_KEY.getValue(), googleAuthService.getApiKey())
    //     .header(GoogleBodyEnum.X_GOOG_FIELD_MASK.getValue(),
    //             "routes.duration,routes.distanceMeters,routes.legs,routes.polyline.encodedPolyline")
    //     .bodyValue(body)
    //     .retrieve()
    //     .bodyToMono(RouteResponse.class)
    //     .block();

    // if (response == null || response.getRoutes().isEmpty()) {
    //     throw new RuntimeException("No route data received from Google Routes API");
    // }

    // return mapToRouteDTO(response, req, routeIdentifier);

    // }
}


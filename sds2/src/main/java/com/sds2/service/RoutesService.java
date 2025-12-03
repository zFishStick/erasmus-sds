package com.sds2.service;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sds2.classes.enums.GoogleBodyEnum;
import com.sds2.classes.request.RouteRequest;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.classes.response.RouteResponse;
import com.sds2.classes.response.RouteResponse.RouteLeg;
import com.sds2.dto.RouteDTO;
import com.sds2.repository.RoutesRepository;
import com.sds2.util.DataISOFormatter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RoutesService {

    private final GoogleAuthService googleAuthService;
    private final RoutesRepository routesRepository;
    private final WebClient.Builder webClientBuilder;

    public String buildComputeRoutesBody(RouteRequest req) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();

        ObjectNode originNode = root.putObject("origin")
                                .putObject("location")
                                .putObject("latLng");
        originNode.put("latitude", req.getOrigin().getLat());
        originNode.put("longitude", req.getOrigin().getLng());

        ObjectNode destNode = root.putObject("destination")
                                .putObject("location")
                                .putObject("latLng");
        destNode.put("latitude", req.getDestination().getLat());
        destNode.put("longitude", req.getDestination().getLng());

        ArrayNode intermediatesNode = root.putArray("intermediates");
        for (WaypointRequest wp : req.getIntermediates()) {
            ObjectNode wpNode = intermediatesNode.addObject();
            ObjectNode loc = wpNode.putObject("location").putObject("latLng");
            loc.put("latitude", wp.getLat());
            loc.put("longitude", wp.getLng());
        }

        root.put("travelMode", req.getTravelMode().name());
        root.put("routingPreference", "TRAFFIC_AWARE");
        root.put("departureTime", DataISOFormatter.formatToISO8601(req.getDepartureTime()));
        root.put("computeAlternativeRoutes", false);

        ObjectNode routeModifiers = root.putObject("routeModifiers");
        routeModifiers.put("avoidTolls", false);
        routeModifiers.put("avoidHighways", false);
        routeModifiers.put("avoidFerries", false);

        root.put("languageCode", "en-US");
        root.put("units", "METRIC");

        return mapper.writeValueAsString(root);
    }

    private RouteDTO mapToRouteDTO(RouteResponse response, String origin, String destination) {
        RouteLeg leg = response.getRoutes().get(0).getLegs()[0];
        return new RouteDTO(
            leg.getDistanceMeters(),
            leg.getDuration(),
            origin,
            destination,
            leg.getPolyline().getEncodedPolyline()
        );
    }

    public RouteDTO computeRoute(RouteRequest req) throws JsonProcessingException {

        String origin = req.getOrigin().getName();
        String destination = req.getDestination().getName();

    // Route route = routesRepository.findByRequest(req);
    // if (route != null) {
    //     return mapToRouteDTO(route);
    // }

    String url = "https://routes.googleapis.com/directions/v2:computeRoutes";

    String body = buildComputeRoutesBody(req);

    Logger logger = Logger.getLogger(RoutesService.class.getName());
    logger.info("Request Body: " + body);

    RouteResponse response = webClientBuilder.build()
        .post()
        .uri(url)
        .header(GoogleBodyEnum.CONTENTTYPE.getValue(), GoogleBodyEnum.APPLICATIONJSON.getValue())
        .header(GoogleBodyEnum.X_GOOG_API_KEY.getValue(), googleAuthService.getApiKey())
        .header(GoogleBodyEnum.X_GOOG_FIELD_MASK.getValue(),
                "routes.duration,routes.distanceMeters,routes.legs,routes.polyline.encodedPolyline")
        .bodyValue(body)
        .retrieve()
        .bodyToMono(RouteResponse.class)
        .block();

    if (response == null || response.getRoutes().isEmpty()) {
        throw new RuntimeException("No route data received from Google Routes API");
    }

    return mapToRouteDTO(response, origin, destination);

    }
}


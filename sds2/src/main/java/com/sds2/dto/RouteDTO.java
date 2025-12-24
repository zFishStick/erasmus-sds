package com.sds2.dto;

import java.util.List;

public record RouteDTO(
    String routeIdentifier,
    String origin,
    String destination,
    List<String> intermediates,
    String travelMode
) {}


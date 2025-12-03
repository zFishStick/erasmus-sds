package com.sds2.dto;

public record RouteDTO(
    int distanceMeters,
    String duration,
    String originName,
    String destinationName,
    String encodedPolyline
) {}


package com.sds2.dto;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.Test;

class RouteDTOTest {
    @Test
    void testRouteDTO() {

    RouteDTO routeDTO = new RouteDTO(
        "routeIdentifier",
        "city",
        "country",
        "origin",
        "destination",
        List.of("intermediate1", "intermediate2"),
        "travelMode"
    );

    assertAll(
        () -> routeDTO.routeIdentifier().equals("routeIdentifier"),
        () -> routeDTO.city().equals("city"),
        () -> routeDTO.country().equals("country"),
        () -> routeDTO.origin().equals("origin"),
        () -> routeDTO.destination().equals("destination"),
        () -> routeDTO.intermediates().equals(List.of("intermediate1", "intermediate2")),
        () -> routeDTO.travelMode().equals("travelMode")
    );

    }
    
}

package com.sds2.classes.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RouteResponse {
    private List<Route> routes;

    @Getter @Setter
    public static class Route {
        private RouteLeg[] legs;
    }

    @Getter @Setter
    public static class RouteLeg {
        private int distanceMeters;
        private String duration;
        private Polyline polyline;
        private String destination;
        private String origin;
    }

    @Getter @Setter
    public static class Polyline {
        private String encodedPolyline;
    }
}



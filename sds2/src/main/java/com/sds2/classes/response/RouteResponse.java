package com.sds2.classes.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RouteResponse {
    private List<RouteData> data;

    @Getter @Setter
    private static class RouteData {
        private Route routes;
    }

    @Getter @Setter
    public static class Route {
        private RouteLabel routeLabel;
        private RouteLeg[] legs;
    }

    public static enum RouteLabel {
        ROUTE_LABEL_UNSPECIFIED,
        DEFAULT_ROUTE,
        DEFAULT_ROUTE_ALTERNATE
    }

    @Getter @Setter
    public static class RouteLeg {
        private int distanceMeters;
        private String duration;
        private Polyline polyline;
        private String description;
        private List<String> warnings;
        private List<Integer> optimizedIntermediateWaypointIndex;
        private RouteLocalizedValues localizedValues;
        private String routeToken;
    }

    @Getter @Setter
    public static class Polyline {
        private String encodedPolyline;
    }

    @Getter @Setter
    public static class RouteLocalizedValues {
        private LocalizedText distance;
        private LocalizedText duration;
    }

    @Getter @Setter
    public static class LocalizedText {
        private String text;
        private String languageCode;
    }
}

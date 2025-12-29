package com.sds2.classes.request;
import com.sds2.classes.routeclasses.RouteTravelMode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RouteRequest {
    private String routeIdentifier;
    private WaypointRequest origin;
    private WaypointRequest destination;
    private WaypointRequest[] intermediates;
    private RouteTravelMode travelMode;
    private String departureTime;
    private Long userId;
}

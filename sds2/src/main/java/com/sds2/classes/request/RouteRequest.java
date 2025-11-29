package com.sds2.classes.request;

import com.google.maps.DirectionsApiRequest.Waypoint;
import com.sds2.classes.routeclasses.RouteTravelMode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class RouteRequest {
    private Waypoint origin;
    private Waypoint destination;
    private Waypoint[] intermediates;
    private RouteTravelMode travelMode;
    private String departureTime;
}

package com.sds2.classes.request;

import com.google.maps.DirectionsApiRequest.Waypoint;

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
    private String duration;
    private String departureTime;
    private String arrivalTime;
    private int distanceMeters;
}

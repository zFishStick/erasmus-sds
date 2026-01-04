package com.sds2.dto;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.entity.Waypoint;

public record WaypointDTO(
    Long id, 
    boolean via, 
    String name, 
    Location location, 
    String address, 
    String destination, 
    String country
) {

    public static WaypointDTO fromEntity(Waypoint waypoint) {
        return new WaypointDTO(
            waypoint.getId(),
            waypoint.isVia(),
            waypoint.getName(),
            waypoint.getLocation(),
            waypoint.getAddress(),
            waypoint.getDestination(),
            waypoint.getCountry()
        );
    }
}


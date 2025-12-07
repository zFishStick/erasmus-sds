package com.sds2.dto;

import com.sds2.classes.coordinates.Location;

public record WaypointDTO(
    Long id, 
    boolean via, 
    String name, 
    Location location, 
    String address, 
    String destination, 
    String country) {
}

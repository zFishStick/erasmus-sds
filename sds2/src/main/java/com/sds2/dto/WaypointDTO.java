package com.sds2.dto;

import com.sds2.classes.coordinates.Location;

public record WaypointDTO(boolean via, String name, Location location, String address) {
}

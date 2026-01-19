package com.sds2.dto;

import org.junit.jupiter.api.Test;

import com.sds2.classes.coordinates.Location;

public class WaypointDTOTest {
    @Test
    public void testWaypointDTO(){
    Long id = 0L; 
    boolean via = true;
    String name = "hello"; 
    Location location = new Location(0.0, 0.0);
    String address = "hello"; 
    String destination = "hello";
    String country = "hello"; 
    WaypointDTO waypointDTO = new WaypointDTO(id, via, name, location, address, destination, country);
    }
}

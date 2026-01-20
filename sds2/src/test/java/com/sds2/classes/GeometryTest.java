package com.sds2.classes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.sds2.classes.coordinates.Location;

class GeometryTest {

    @Test
    void createGeometryInstance() {

        Location location = new Location(10.0, 20.0);

        Geometry geometry = new Geometry();
        geometry.setLocation(location);

        assertEquals(location, geometry.getLocation());
    }
    
}

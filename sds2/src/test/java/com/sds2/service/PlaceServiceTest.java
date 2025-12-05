package com.sds2.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sds2.classes.coordinates.Location;
import com.sds2.dto.PlacesDTO;

@SpringBootTest
class PlaceServiceTest {

    @Autowired
    private PlaceService placeService;

    @Test
    void searchNearbyTest() {
        double latitude = 52.405678599999995;
        double longitude = 16.9312766;

        Location location = new Location(latitude, longitude);

        List<PlacesDTO> response = placeService.searchNearby(location, "Poznan", "Poland");
        assertNotNull(response);
        System.out.println(response);
    }
}

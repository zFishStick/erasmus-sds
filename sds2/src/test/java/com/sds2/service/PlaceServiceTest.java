package com.sds2.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sds2.classes.Location;
import com.sds2.classes.response.PlaceResponse;
import com.sds2.dto.PlacesDTO;

@SpringBootTest
class PlaceServiceTest {

    @Autowired
    private PlaceService placeService;

    // @Test
    // void findPlaceFromTextTest() {
    //     String place = "Poznan,%20Poland";
    //     PlaceResponse response = placeService.searchText(place);
    //     assertNotNull(response);
    //     System.out.println("Result " + response.getPlaces()); // place_id: ChIJtwrh7NJEBEcR0b80A5gx6qQ
    // }

    @Test
    void searchNearbyTest() {
        double latitude = 52.405678599999995;
        double longitude = 16.9312766;

        Location location = new Location(latitude, longitude);

        List<PlacesDTO> response = placeService.searchNearby(location, "Poznan", "Poland");
        assertNotNull(response);
        System.out.println(response);
    }

    // @Test
    // void addRemainingPlacesTest() {
    //     Location location = new Location(52.405678599999995, 16.9312766);
    //     List<PlacesDTO> remainingPlaces = placeService.addRemainingNearbyPlaces(location, "Poznan", "Poland");
    //     assertNotNull(remainingPlaces);
    // }
}

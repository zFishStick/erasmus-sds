package com.sds2.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.enums.GoogleBodyEnum;
import com.sds2.classes.response.PlaceResponse;
import com.sds2.dto.PlacesDTO;

@SpringBootTest
class PlaceServiceTest {

    @Autowired
    private PlaceService placeService;
    @Autowired
    private GoogleAuthService googleAuthService;
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Test
    void searchNearbyTest() {
        double latitude = 52.405678599999995;
        double longitude = 16.9312766;

        Location location = new Location(latitude, longitude);

        List<PlacesDTO> response = placeService.searchNearby(location, "Poznan", "Poland");
        assertNotNull(response);
        System.out.println(response);
    }

    @Test
    void addOtherPlaces() {
        String city = "Rome";
        String country = "Italy";
        double latitude = 41.89193;
        double longitude = 12.51133;

        Location location = new Location(latitude, longitude);

        List<PlacesDTO> response = placeService.searchNearby(location, city, country);
        assertNotNull(response);
        System.out.println(response);   
    }
}

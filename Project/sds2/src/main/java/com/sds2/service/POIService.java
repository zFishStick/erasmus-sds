package com.sds2.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sds2.classes.Coordinates;
import com.sds2.classes.POI;
import com.sds2.classes.response.POISResponse;
import com.sds2.controller.AmadeusPageController;
import com.sds2.repository.POIRepository;

@Service
public class POIService {
    private final POIRepository poiRepository;
    private AmadeusPageController amadeusPageController;

    public POIService(POIRepository poiRepository) {
        this.poiRepository = poiRepository;
        this.amadeusPageController = new AmadeusPageController();
    }

    public void addPOI(POI poi) {
        if (poi == null) {
            throw new IllegalArgumentException("POI cannot be null");
        }
        poiRepository.addPOI(poi);
    }

    public POI getPOIById(Long id) {
        return poiRepository.findById(id);
    }

    public List<POI> getPoisByCityOrCoordinates(String city, Coordinates coordinates) {
        List<POI> activities =  poiRepository.findByCity(city);
        if (activities.isEmpty()) {
            activities = getPointOfInterests(coordinates);
            activities.forEach(this::addPOI);
        }
        return activities;
    }

    private List<POI> getPointOfInterests(Coordinates coordinates) {
        double latitude = coordinates.getCoordinates()[0];
        double longitude = coordinates.getCoordinates()[1];

        String uri = String.format(
            "https://api.amadeus.com/v1/shopping/activities?latitude=%f&longitude=%f",
            latitude, longitude
        );

        POISResponse response = amadeusPageController.getWebClientBuilder().build()
            .get()
            .uri(uri)
            .header("Authorization", "Bearer " + amadeusPageController.getAuthService().getAccessToken())
            .retrieve()
            .bodyToMono(POISResponse.class)
            .block();

        if (response == null) {
            throw new IllegalStateException("Failed to retrieve activities from Amadeus API");
        }

        return response.getData();
    }
    
}

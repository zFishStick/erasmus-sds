package com.sds2.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.Coordinates;
import com.sds2.classes.POI;
import com.sds2.classes.response.POISResponse;
import com.sds2.repository.POIRepository;

@Service
public class POIService {
    private final POIRepository poiRepository;
    private final AmadeusAuthService amadeusAuthService;
    private final WebClient.Builder webClientBuilder;

    public POIService(
        POIRepository poiRepository, 
        AmadeusAuthService amadeusAuthService, 
        WebClient.Builder webClientBuilder
    ) {
        this.poiRepository = poiRepository;
        this.amadeusAuthService = amadeusAuthService;
        this.webClientBuilder = webClientBuilder;
    }

    public void addPOI(POI poi) {
        if (poi == null) {
            throw new IllegalArgumentException("POI cannot be null");
        }
        poiRepository.save(poi);
    }

    public POI getPOIById(long id) {
        return poiRepository.findById(id);
    }

    public List<POI> getPoisByCityOrCoordinates(String city, Coordinates coordinates) {
        List<POI> activities =  poiRepository.findByCityName(city);
        if (activities.isEmpty()) {
            activities = getPointOfInterests(coordinates);
            activities.forEach(this::addPOI);
        }
        return activities;
    }

    private List<POI> getPointOfInterests(Coordinates coordinates) {
        double latitude = coordinates.getCoordinates()[0];
        double longitude = coordinates.getCoordinates()[1];

        String uri = "https://api.amadeus.com/v1/shopping/activities?latitude=%f&longitude=%f".formatted(
                latitude, longitude);

        POISResponse response = webClientBuilder
            .build()
            .get()
            .uri(uri)
            .header("Authorization", "Bearer " + amadeusAuthService.getAccessToken())
            .retrieve()
            .bodyToMono(POISResponse.class)
            .block();

        if (response == null) {
            throw new IllegalStateException("Failed to retrieve activities from Amadeus API");
        }

        return response.getData();
    }
    
}

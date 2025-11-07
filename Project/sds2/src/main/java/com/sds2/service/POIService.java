package com.sds2.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.GeoCode;
import com.sds2.classes.Price;
import com.sds2.classes.response.POISResponse;
import com.sds2.dto.POIDTO;
import com.sds2.repository.POIRepository;

import com.sds2.classes.poi.POI;
import com.sds2.classes.poi.POIInfo;

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

    public List<POIDTO> getPoisByCoordinates(GeoCode coordinates) {
        // Retrieve from database if exists
        List<POI> activities =  poiRepository.findByCoordinates_LatitudeAndCoordinates_Longitude(
            coordinates.getLatitude(), coordinates.getLongitude());
            if (!activities.isEmpty()) {
                Logger.getLogger(POIService.class.getName()).info("Retrieved POIs from database.");
                return activities.stream()
                    .map(this::mapToDTO)
                    .toList();
            }

            return getPointOfInterests(coordinates);
    }

    private List<POIDTO> getPointOfInterests(GeoCode coordinates) {
        double latitude = coordinates.getLatitude();    
        double longitude = coordinates.getLongitude();

        List<POI> existingPOIs = poiRepository.findByCoordinates_LatitudeAndCoordinates_Longitude(latitude, longitude);
        if (!existingPOIs.isEmpty()) {
            return existingPOIs.stream()
                .map(this::mapToDTO)
                .toList();
        }

        String uri = "https://api.amadeus.com/v1/shopping/activities?latitude=%f&longitude=%f".formatted(
                latitude, longitude
        );

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

        return mapToDTOs(response);
    }

    private List<POIDTO> mapToDTOs(POISResponse response) {
        return response.getData().stream()
            .map(data -> {
                POI poi = new POI(
                    new POIInfo(data.getName(), data.getDescription()),
                    data.getType(),
                    data.getPrice(),
                    data.getPictures().get(0),
                    data.getMinimumDuration(),
                    data.getBookingLink(),
                    data.getGeoCode()
                );

                poi.setBookingLink(data.getBookingLink());
                if (data.getPictures() != null && !data.getPictures().isEmpty()) {
                    poi.setPicture(data.getPictures().get(0));
                    }
                poiRepository.save(poi);
                return mapToDTO(poi);
            })
            .toList();
    }

    private POIDTO mapToDTO(POI poi) {
        String name = poi.getName();
        String description = poi.getDescription();
        String type = poi.getType();
        Price price = poi.getPrice();
        String minimumDuration = poi.getMinimumDuration();
        String bookingLink = poi.getBookingLink();
        String pictures = poi.getPictures();
        return new POIDTO(name, description, type, price, pictures, minimumDuration, bookingLink);
    }
    
}

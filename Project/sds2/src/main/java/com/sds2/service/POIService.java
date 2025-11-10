package com.sds2.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public List<POIDTO> getPointOfInterests(GeoCode coordinates, String cityName, String countryCode) {
        List<POI> activities =  poiRepository.findByCityNameOrCountryCode(cityName, countryCode);
        if (!activities.isEmpty()) {
            Logger.getLogger(POIService.class.getName()).info("Retrieved POIs from database.");
            return activities.stream()
                .map(this::mapToDTO)
                    .toList();
            } else {
                Logger.getLogger(POIService.class.getName()).info("No POIs found in database for given coordinates.");
            }

            return getPointOfInterestsByAPI(coordinates, cityName, countryCode);
    }

    private List<POIDTO> getPointOfInterestsByAPI(GeoCode coordinates, String cityName, String countryCode) {
        // double latitude = coordinates.getLatitude();
        // double longitude = coordinates.getLongitude();

        Map<String, Double> bbox = calculateBoundingBox(coordinates, 0.005);

        String uriString = String.format(Locale.US,"https://api.amadeus.com/v1/shopping/activities/by-square?north=%f&west=%f&south=%f&east=%f",
        bbox.get("north"), bbox.get("west"), bbox.get("south"), bbox.get("east"));

        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid URI syntax: " + uriString, e);
        }
        
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

        return mapToDTOs(response, cityName, countryCode);
    }

    private List<POIDTO> mapToDTOs(POISResponse response, String cityName, String countryCode) {
        return response.getData().stream()
            .map(data -> {
                POI poi = new POI(
                    cityName,
                    countryCode,
                    new POIInfo(data.getName(), data.getType(), data.getDescription(), data.getPictures().get(0), data.getMinimumDuration(), data.getBookingLink()),
                    data.getPrice(),
                    data.getGeoCode()
                );

                poi.getInfo().setBookingLink(data.getBookingLink());
                if (data.getPictures() != null && !data.getPictures().isEmpty()) {
                    poi.getInfo().setPictures(data.getPictures().get(0));
                }
                poiRepository.save(poi);
                return mapToDTO(poi);
            })
            .toList();
    }

    private POIDTO mapToDTO(POI poi) {
        String cityName = poi.getCityName();
        String name = poi.getName();
        String description = poi.getDescription();
        String type = poi.getType();
        Price price = poi.getPrice();
        String minimumDuration = poi.getInfo().getMinimumDuration();
        String bookingLink = poi.getInfo().getBookingLink();
        String pictures = poi.getInfo().getPictures();
        return new POIDTO(cityName, name, description, type, price, pictures, minimumDuration, bookingLink);
    }

    public Map<String, Double> calculateBoundingBox(GeoCode center, double distanceKm) {
        final double EARTH_RADIUS_KM = 6371.0;
        double deltaLat = Math.toDegrees(distanceKm / EARTH_RADIUS_KM);
        double deltaLon = Math.toDegrees(distanceKm / (EARTH_RADIUS_KM * Math.cos(Math.toRadians(center.getLatitude()))));

        double north = center.getLatitude() + deltaLat;
        double south = center.getLatitude() - deltaLat;
        double east = center.getLongitude() + deltaLon;
        double west = center.getLongitude() - deltaLon;
        
        return Map.of(
            "north", north,
            "south", south,
            "east", east,
            "west", west
        );
    }
}

package com.sds2.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.entity.POI;
import com.sds2.classes.poi.POIInfo;
import com.sds2.classes.response.POISResponse;
import com.sds2.dto.POIDTO;
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

    public List<POIDTO> getPointOfInterests(GeoCode coordinates, String cityName, String countryCode) {
        List<POI> activities =  poiRepository.findByCityNameAndCountryCode(cityName, countryCode);
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

        int radius = 1; // 0 to 20

        String uriString = String.format(Locale.US,
        "https://api.amadeus.com/v1/shopping/activities?latitude=%f&longitude=%f&radius=%d",
        coordinates.getLatitude(), coordinates.getLongitude(), radius);

        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid URI syntax: " + uriString, e);
        }
        
        ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16 MB
            .build();

        POISResponse response = webClientBuilder
            .build()
            .mutate()
            .exchangeStrategies(strategies)
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
        if (response == null || response.getData() == null) {
            return List.of();
        }

        return response.getData().stream()
            .map(data -> {
                POIInfo info = new POIInfo(
                    data.getName(),
                    data.getType(),
                    data.getDescription(),
                    data.getPictures() != null && !data.getPictures().isEmpty() ? data.getPictures().get(0) : null,
                    data.getMinimumDuration(),
                    data.getBookingLink()
                );

                POI poi = new POI(cityName, countryCode, info, data.getPrice(), data.getGeoCode());
                addPOI(poi);
                return mapToDTO(poi);
            })
            .toList();
    }

    private POIDTO mapToDTO(POI poi) {
        return new POIDTO(
            poi.getCityName(),
            poi.getInfo().getName(),
            poi.getInfo().getDescription(),
            poi.getType(),
            poi.getPrice(),
            poi.getInfo().getPictures(),
            poi.getInfo().getMinimumDuration(),
            poi.getInfo().getBookingLink()
        );
    }

}

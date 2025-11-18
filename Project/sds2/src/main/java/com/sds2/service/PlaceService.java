package com.sds2.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.CitySummary;
import com.sds2.classes.Location;
import com.sds2.classes.Places;

import com.sds2.classes.response.PhotoResponse;
import com.sds2.classes.response.PlaceResponse;
import com.sds2.dto.PlacesDTO;
import com.sds2.repository.PlacesRepository;

@Service
public class PlaceService {

    private final PlacesRepository placesRepository;
    private final GoogleAuthService googleAuthService;
    private final WebClient.Builder webClientBuilder;

    public PlaceService(
        PlacesRepository placesRepository,
        GoogleAuthService googleAuthService,
        WebClient.Builder webClientBuilder
    ) {
        this.placesRepository = placesRepository;
        this.googleAuthService = googleAuthService;
        this.webClientBuilder = webClientBuilder;
    }

    public void addPlace(Places place) {
        if (place == null) {
            throw new IllegalArgumentException("Place cannot be null");
        }
        placesRepository.save(place);
    }

    public PlaceResponse searchText(String query) {

        String url = "https://places.googleapis.com/v1/places:searchText";

        String[] headerInfo = {
            "places.id",
            "places.name",
            "places.displayName.text",
            "places.primaryType",
            "places.formattedAddress",
            "places.location",
            "places.addressComponents",
            "places.rating",
            "places.photos.name",
            "places.priceRange"
        };

        String textQueryBody = """
        {
            "textQuery": "%s"
        }
        """.formatted(query);

        PlaceResponse response = webClientBuilder.build()
                .post()
                .uri(url)
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("X-Goog-Api-Key", googleAuthService.getApiKey())
                .header("X-Goog-FieldMask", String.join(",", headerInfo))
                .bodyValue(textQueryBody)
                .retrieve()
                .bodyToMono(PlaceResponse.class)
                .block();

        if (response == null) {
            throw new IllegalStateException("Failed to retrieve place from Google Places API");
        }

        return response;
    }

    private List<PlacesDTO> mapPlacesToDTOs(PlaceResponse response, String city, String country) {
        return response.getPlaces().stream()
            .map(data -> {                
                Places places = Places.builder()
                    .citySummary(new CitySummary(city, country))
                    .text(data.getName())
                    .photoUrl(getPlacePhoto(data.getPhotos()[0].getName()))
                    .type(data.getPrimaryType())
                    .address(data.getFormattedAddress())
                    .location(data.getLocation())
                    .rating(data.getRating())
                    .priceRange(data.getPriceRange())
                    .build();

            addPlace(places);
            return mapToDTO(places);
        })
            .toList();
    }

    private PlacesDTO mapToDTO(Places places) {
        return new PlacesDTO(
            places.getText(),
            places.getPhotoUrl(),
            places.getType(),
            places.getAddress(),
            places.getLocation(),
            places.getRating(),
            places.getPriceRange()
        );
    }

    public List<PlacesDTO> searchNearby(Location location, String city, String country) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String url = "https://places.googleapis.com/v1/places:searchNearby";

        String[] headerInfo = {
            "places.id",
            "places.name",
            "places.displayName.text",
            "places.primaryType",
            "places.formattedAddress",
            "places.location",
            "places.addressComponents",
            "places.rating",
            "places.photos.name", // photoReference
            "places.priceRange"
        };

        String body = """
        {
        "locationRestriction": {
            "circle": {
            "center": {
                "latitude": %f,
                "longitude": %f
            },
            "radius": 1000.0
            }
        }
        }
        """.formatted(latitude, longitude);

        PlaceResponse response = webClientBuilder.build()
                .post()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", googleAuthService.getApiKey())
                .header("X-Goog-FieldMask", String.join(",", headerInfo))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PlaceResponse.class)
                .block();

        if (response == null) {
            throw new IllegalStateException("Failed to retrieve nearby places from Google Places API");
        }

        return mapPlacesToDTOs(response, city, country);
    }

    public String getPlacePhoto(String photoName) {

        String uriString = String.format(
            "https://places.googleapis.com/v1/%s/media?maxWidthPx=400&key=%s",
            photoName,
            googleAuthService.getApiKey()
        );

        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid URI syntax: " + uriString, e);
        }

        PhotoResponse response = webClientBuilder
            .build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono(PhotoResponse.class)
            .block();

        if (response == null) {
            throw new IllegalStateException("Failed to retrieve place photo from Google Places API");
        }

        return response.getPhotoUri();
    }

}

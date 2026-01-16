package com.sds2.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.CitySummary;
import com.sds2.classes.Places;
import com.sds2.classes.coordinates.Location;
import com.sds2.classes.enums.GoogleBodyEnum;
import com.sds2.classes.response.PhotoResponse;
import com.sds2.classes.response.PlaceResponse;
import com.sds2.classes.response.PlaceResponse.Photo;
import com.sds2.dto.PlacesDTO;
import com.sds2.repository.PlacesRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PlaceService {
    
    private final PlacesRepository placesRepository;
    private final GoogleAuthService googleAuthService;
    private final WebClient.Builder webClientBuilder;

    private static final String[] HEADER_INFO = {
        "places.id",
        "places.name",
        "places.displayName.text",
        "places.primaryType",
        "places.formattedAddress",
        "places.location",
        "places.addressComponents",
        "places.rating",
        "places.photos.name",
        "places.priceRange",
        "places.websiteUri"
    };

    public void addPlace(Places place) {
        if (place == null) {
            throw new IllegalArgumentException("Place cannot be null");
        }

        Places existingPlace = placesRepository.findByName(place.getName());
        System.out.println("Checking existence for place: " + place.getName());
        if (existingPlace != null) {
            return; 
        }

        placesRepository.save(place);
    }

    public Places findPlaceByName(String name) {
        Places p = placesRepository.findByText(name);
        if (p == null) {
            throw new IllegalStateException("Place with name " + name + " not found");
        }
        return p;
    }

    public List<PlacesDTO> mapPlacesToDTOs(PlaceResponse response, String city, String country) {
        return response.getPlaces().stream()
            .map(data -> {                
                Places places = Places.builder()
                    .citySummary(new CitySummary(city, country))
                    .name(data.getName())
                    .text(data.getDisplayName().getText())
                    .photoUrl(getPlacePhoto(data.getPhotos()))
                    .type(data.getPrimaryType())
                    .address(data.getFormattedAddress())
                    .location(data.getLocation())
                    .rating(data.getRating())
                    .priceRange(data.getPriceRange())
                    .websiteUri(data.getWebsiteUri())
                    .build();

                addPlace(places);
                return mapToDTO(places);
            })
            .toList();
    }


    public PlacesDTO mapToDTO(Places places) {
        return new PlacesDTO(
            places.getId(),
            places.getText(),
            places.getPhotoUrl(),
            places.getType(),
            places.getAddress(),
            places.getLocation(),
            places.getRating(),
            places.getPriceRange(),
            places.getWebsiteUri()
        );
    }

    public List<PlacesDTO> searchNearby(Location location, String city, String country) {
        List<Places> existingPlaces = placesRepository.findByCitySummary_CityAndCitySummary_Country(city, country);
        if (!existingPlaces.isEmpty()) {
            return existingPlaces.stream()
                .map(this::mapToDTO)
                .toList();
        }
        Map<String, Object> body = buildNearbyBody(location, 10000.0);
        PlaceResponse response = callPlacesApi("https://places.googleapis.com/v1/places:searchNearby", body);
        return mapPlacesToDTOs(response, city, country);
    }

    public List<PlacesDTO> searchByText(Location location, String city, String country, String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        Map<String, Object> body = buildTextSearchBody(query, location, 12000.0);
        PlaceResponse response = callPlacesApi("https://places.googleapis.com/v1/places:searchText", body);
        return mapPlacesToDTOs(response, city, country);
    }

    public List<String> getPlacePhoto(Photo[] photos) {
        if (photos == null || photos.length == 0) {
            return List.of();
        }

        return Arrays.stream(photos)
                    .limit(3)
                    .map(photo -> fetchPhotoUri(photo.getName()))
                    .toList();
    }

    private PlaceResponse callPlacesApi(String url, Object body) {
        PlaceResponse response = webClientBuilder.build()
            .post()
            .uri(url)
            .header(GoogleBodyEnum.CONTENTTYPE.getValue(), GoogleBodyEnum.APPLICATIONJSON.getValue())
            .header(GoogleBodyEnum.X_GOOG_API_KEY.getValue(), googleAuthService.getApiKey())
            .header(GoogleBodyEnum.X_GOOG_FIELD_MASK.getValue(), String.join(",", HEADER_INFO))
            .bodyValue(body)
            .retrieve()
            .bodyToMono(PlaceResponse.class)
            .block();

        if (response == null) {
            throw new IllegalStateException("Failed to retrieve places from Google Places API");
        }

        return response;
    }

    private Map<String, Object> buildNearbyBody(Location location, double radius) {
        return Map.of(
            "locationRestriction", Map.of(
                "circle", Map.of(
                    "center", Map.of(
                        "latitude", location.getLatitude(),
                        "longitude", location.getLongitude()
                    ),
                    "radius", radius
                )
            )
        );
    }

    private Map<String, Object> buildTextSearchBody(String query, Location location, double radius) {
        return Map.of(
            "textQuery", query,
            "locationBias", Map.of(
                "circle", Map.of(
                    "center", Map.of(
                        "latitude", location.getLatitude(),
                        "longitude", location.getLongitude()
                    ),
                    "radius", radius
                )
            )
        );
    }

    private String fetchPhotoUri(String photoName) {
        String uriString = String.format(
            "https://places.googleapis.com/v1/%s/media?maxWidthPx=400&key=%s",
            photoName,
            googleAuthService.getApiKey()
        );

        try {
            URI uri = new URI(uriString);
            PhotoResponse response = webClientBuilder
                    .build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(PhotoResponse.class)
                    .block();

            if (response == null || response.getPhotoUri() == null) {
                throw new IllegalStateException("Failed to retrieve photoUri from Google API for " + photoName);
            }

            return response.getPhotoUri();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid URI syntax: " + uriString, e);
        }
    }
}

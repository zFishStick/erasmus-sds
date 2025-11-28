package com.sds2.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.CitySummary;
import com.sds2.classes.Location;
import com.sds2.classes.Places;
import com.sds2.classes.response.PhotoResponse;
import com.sds2.classes.response.PlaceResponse;
import com.sds2.classes.response.PlaceResponse.Photo;
import com.sds2.classes.response.PlaceResponse.PlacesData;
import com.sds2.dto.PlacesDTO;
import com.sds2.repository.PlacesRepository;

@Service
public class PlaceService {

    private static final String CONTENTTYPE = "Content-Type";
    private static final String APPLICATIONJSON = "application/json; charset=UTF-8";
    private static final String X_GOOG_API_KEY = "X-Goog-Api-Key";
    private static final String X_GOOG_FIELD_MASK = "X-Goog-FieldMask";

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

    private String [] headerInfo = {
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
        placesRepository.save(place);
    }

    public Places findPlaceByText(String text) {
        Places p = placesRepository.findByText(text);
        if (p == null) {
            throw new IllegalStateException("Place with text " + text + " not found");
        }
        return p;
    }

    public Places findPlaceByName(String name) {
        Places p = placesRepository.findByText(name);
        if (p == null) {
            throw new IllegalStateException("Place with name " + name + " not found");
        }
        return p;
    }

    public PlaceResponse searchText(String query) {

        String url = "https://places.googleapis.com/v1/places:searchText";

        String textQueryBody = """
        {
            "textQuery": "%s"
        }
        """.formatted(query);

        PlaceResponse response = webClientBuilder.build()
                .post()
                .uri(url)
                .header(CONTENTTYPE, APPLICATIONJSON)
                .header(X_GOOG_API_KEY, googleAuthService.getApiKey())
                .header(X_GOOG_FIELD_MASK, String.join(",", headerInfo))
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
            .filter(data -> matchesCityAndCountry(data, city, country))
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

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        List<Places> existingPlaces = placesRepository.findByCitySummary_CityAndCitySummary_Country(city, country);

        if (!existingPlaces.isEmpty()) {
            return existingPlaces.stream()
                .map(this::mapToDTO)
                .toList();
        }

        String url = "https://places.googleapis.com/v1/places:searchNearby";

        // radius in meters
        double radius = 10000.0;

        String body = String.format(Locale.US, """
        {
          "locationRestriction": {
            "circle": {
            "center": {
                "latitude": %f,
                "longitude": %f
            },
            "radius": %f
            }
          }
        }
        }
        """.formatted(latitude, longitude, radius));

        PlaceResponse response = webClientBuilder.build()
                .post()
                .uri(url)
                .header(CONTENTTYPE, APPLICATIONJSON)
                .header(X_GOOG_API_KEY, googleAuthService.getApiKey())
                .header(X_GOOG_FIELD_MASK, String.join(",", headerInfo))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PlaceResponse.class)
                .block();

        if (response == null) {
            throw new IllegalStateException("Failed to retrieve nearby places from Google Places API");
        }

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

    private static String normalize(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                        .toLowerCase();
    }

    private boolean matchesCityAndCountry(PlacesData data, String city, String country) {
        if (data.getAddressComponents() == null) return false;

        String normCity = normalize(city);
        String normCountry = normalize(country);

        boolean cityMatch = Arrays.stream(data.getAddressComponents())
            .filter(Objects::nonNull)
            .anyMatch(ac ->
                ac.getTypes() != null &&    
                ac.getLongText() != null && 
                Arrays.stream(ac.getTypes())
                    .filter(Objects::nonNull) 
                    .anyMatch(t -> t.equals("locality")) &&
                normalize(ac.getLongText()).equals(normCity)
            );

        boolean countryMatch = Arrays.stream(data.getAddressComponents())
            .filter(Objects::nonNull)
            .anyMatch(ac ->
                ac.getTypes() != null &&
                ac.getLongText() != null &&
                Arrays.stream(ac.getTypes())
                    .filter(Objects::nonNull)
                    .anyMatch(t -> t.equals("country")) &&
                normalize(ac.getLongText()).equals(normCountry)
            );

        return cityMatch && countryMatch;
    }

    public List<PlacesDTO> addRemainingNearbyPlaces(Location location, String city, String country) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String url = "https://places.googleapis.com/v1/places:searchNearby";
        
        // radius in meters
        double radius = 10000.0;

        String body = """
        {
        "locationRestriction": {
            "circle": {
            "center": {
                "latitude": %f,
                "longitude": %f
            },
            "radius": %f
            }
        }
        }
        """.formatted(latitude, longitude, radius);

        PlaceResponse response = webClientBuilder.build()
                .post()
                .uri(url)
                .header(CONTENTTYPE, APPLICATIONJSON)
                .header(X_GOOG_API_KEY, googleAuthService.getApiKey())
                .header(X_GOOG_FIELD_MASK, String.join(",", headerInfo))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PlaceResponse.class)
                .block();

        if (response == null) {
            throw new IllegalStateException("Failed to retrieve nearby places from Google Places API");
        }

        return mapPlacesToDTOs(response, city, country);
    }



}

package com.sds2.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.City;
import com.sds2.classes.response.CityResponse;
import com.sds2.dto.CityDTO;
import com.sds2.repository.CityRepository;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final AmadeusAuthService amadeusAuthService;
    private final WebClient.Builder webClientBuilder;

    public CityService(
        CityRepository cityRepository,
        AmadeusAuthService amadeusAuthService,
        WebClient.Builder webClientBuilder
    ) {
        this.cityRepository = cityRepository;
        this.amadeusAuthService = amadeusAuthService;
        this.webClientBuilder = webClientBuilder;
    }

    public List<CityDTO> getCity(String name) throws IOException {

        List<City> cities = cityRepository.findByNameStartingWithIgnoreCase(name);

        if (!cities.isEmpty()) {
            return cities.stream()
                    .map(this::mapToDTO)
                    .toList();
        }
        
        String uriString = String.format(Locale.US, "https://api.amadeus.com/v1/reference-data/locations/cities?keyword=%s&max=5",
                URLEncoder.encode(name, StandardCharsets.UTF_8));

        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid URI syntax: " + uriString, e);
        }
        
        CityResponse response = webClientBuilder
                .build()
                .get()
                .uri(uri)
                .header("Authorization", "Bearer " + amadeusAuthService.getAccessToken())
                .retrieve()
                .bodyToMono(CityResponse.class)
                .block();

        if (response == null || response.getData() == null) {
            throw new IOException("Failed to retrieve city data from Amadeus API");
        }

        return mapToDTOs(response);
    }

    private List<CityDTO> mapToDTOs(CityResponse response) {
        return response.getData().stream()
                .filter(c -> {
                    if (c.getGeoCode() == null) return false;
                    Double lat = c.getGeoCode().getLatitude();
                    Double lon = c.getGeoCode().getLongitude();
                    return lat != null && lon != null && (Double.compare(lat, 0.0) != 0 || Double.compare(lon, 0.0) != 0);
                })
                .map(c -> {
                    City city = new City(
                            c.getName(),
                            c.getAddress() != null ? c.getAddress().getCountryCode() : "N/A",
                            c.getGeoCode().getLatitude(),
                            c.getGeoCode().getLongitude(),
                            c.getIataCode()
                    );
                    city = cityRepository.save(city);
                    return mapToDTO(city);
                })
                .toList();
    }

    private CityDTO mapToDTO(City city) {
        String country = (city.getCountry() != null) ? city.getCountry() : "N/A";
        double latitude = city.getCoordinates().getLatitude();
        double longitude = city.getCoordinates().getLongitude();
        String iataCode = city.getIataCode();
        return new CityDTO(city.getName(), country, latitude, longitude, iataCode);
    }

}


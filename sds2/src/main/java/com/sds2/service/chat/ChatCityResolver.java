package com.sds2.service.chat;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sds2.dto.ChatItineraryRequest;
import com.sds2.dto.CityDTO;
import com.sds2.service.CityService;

@Service
public class ChatCityResolver {
    private final CityService cityService;

    public ChatCityResolver(CityService cityService) {
        this.cityService = cityService;
    }

    public CityContext resolve(ChatItineraryRequest request) {
        Double latitude = request.latitude();
        Double longitude = request.longitude();
        String destination = request.destination().trim();
        String country = request.countryCode();

        if (latitude != null && longitude != null) {
            return new CityContext(destination, country, latitude, longitude);
        }

        try {
            List<CityDTO> cities = cityService.getCity(destination);
            if (cities == null || cities.isEmpty()) {
                throw new IllegalArgumentException("Destination not found.");
            }
            CityDTO city = cities.get(0);
            String resolvedCountry = (country == null || country.isBlank()) ? city.country() : country;
            return new CityContext(destination, resolvedCountry, city.latitude(), city.longitude());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to resolve destination coordinates.", ex);
        }
    }
}

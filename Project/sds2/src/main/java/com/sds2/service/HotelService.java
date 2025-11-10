package com.sds2.service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sds2.classes.Hotel;
import com.sds2.classes.response.HotelResponse;
import com.sds2.dto.HotelDTO;
import com.sds2.repository.HotelRepository;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final AmadeusAuthService amadeusAuthService;
    private final WebClient.Builder webClientBuilder;

    public HotelService(
        HotelRepository hotelRepository, 
        AmadeusAuthService amadeusAuthService, 
        WebClient.Builder webClientBuilder
    ) {
        this.hotelRepository = hotelRepository;
        this.amadeusAuthService = amadeusAuthService;
        this.webClientBuilder = webClientBuilder;
    }

    public void addHotel(Hotel hotel) {
        if (hotel == null) {
            throw new IllegalArgumentException("Hotel cannot be null");
        }
        hotelRepository.save(hotel);
    }

    public List<HotelDTO> getHotelsByIataCode(String cityName) {

        String iataCode = convertToIataCode(cityName); //I.E "POZ" for Poznan

        List<Hotel> hotels = hotelRepository.findByIataCode(iataCode);

        if (!hotels.isEmpty()) {
            return hotels.stream()
                .map(this::mapToDTO)
                .toList();
        } else {
            Logger.getLogger(HotelService.class.getName()).info("No hotels found in database for given destination.");
        }

        return getHotelsByIataCodeFromAPI(iataCode, cityName);

    }

    private List<HotelDTO> getHotelsByIataCodeFromAPI(String iataCode, String cityName) {
    
        String url = String.format(Locale.US,
         "https://api.amadeus.com/v1/reference-data/locations/hotels/by-city?radius=2&cityCode=%s", 
         iataCode);

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            Logger.getLogger(HotelService.class.getName()).severe("Invalid URI syntax: " + e.getMessage());
            return List.of();
        }

        HotelResponse response = webClientBuilder
            .build()
            .get()
            .uri(uri)
            .header("Authorization", "Bearer " + amadeusAuthService.getAccessToken())
            .retrieve()
            .bodyToMono(HotelResponse.class)
            .block();

        if (response == null || response.getData() == null) {
            throw new IllegalStateException("Failed to retrieve hotels from API: response data is null");
        }

        return mapToDTOs(response, iataCode, cityName);
    }

    private List<HotelDTO> mapToDTOs(HotelResponse response, String iataCode, String cityName) {
        return response.getData().stream()
            .map(data -> {
                Hotel hotel = new Hotel(
                    data.getHotelId(),
                    data.getName(),
                    cityName,
                    iataCode,
                    data.getAddress()
                );
                addHotel(hotel);
                return mapToDTO(hotel);
            })
            .toList();
    }

    private HotelDTO mapToDTO(Hotel hotel) {
    return new HotelDTO(
        hotel.getName(),
        hotel.getDestination(),
        hotel.getAddress().getCountryCode(),
        hotel.getAddress().getLines()
    );
}

    private String convertToIataCode(String input) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Map<String, String>> airports = mapper.readValue(new File("airports.json"), Map.class);
            String iataCode = airports.get(input).get("iata");
            return iataCode != null ? iataCode : input;
        } catch (Exception e) {
            Logger.getLogger(HotelService.class.getName()).severe("Error reading airports.json: " + e.getMessage());
            return input;
        }
    }

    
}

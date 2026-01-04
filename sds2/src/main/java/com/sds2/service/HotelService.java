package com.sds2.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.Price;
import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.entity.Hotel;
import com.sds2.classes.hotel.HotelAddress;
import com.sds2.classes.response.HotelResponse;
import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelDetailsDTO;
import com.sds2.dto.HotelOfferDTO;
import com.sds2.repository.HotelRepository;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final HotelOfferService hotelOfferService;
    private final AmadeusAuthService amadeusAuthService;
    private final WebClient.Builder webClientBuilder;

    public HotelService(
            AmadeusAuthService amadeusAuthService,
            HotelOfferService hotelOfferService,
            HotelRepository hotelRepository,
            WebClient.Builder webClientBuilder
    ) {
        this.amadeusAuthService = amadeusAuthService;
        this.hotelOfferService = hotelOfferService;
        this.hotelRepository = hotelRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public void addHotel(Hotel hotel) {
        if (hotel == null) {
            throw new IllegalArgumentException("Hotel cannot be null");
        }

        if (hotel.getHotelId() != null) {
            Hotel existing = hotelRepository.findByHotelId(hotel.getHotelId());
            if (existing != null) {
                return;
            }
        }

        hotelRepository.save(hotel);
    }

    public List<HotelDetailsDTO> getHotelById(String hotelId, int adults, String checkInDate, String checkOutDate) {
        Hotel hotel = hotelRepository.findByHotelId(hotelId);

        if (hotel == null) {
            return List.of();
        }

        List<HotelOfferDTO> offers = hotelOfferService.getOffersByHotelId(hotelId, adults, checkInDate, checkOutDate);

        HotelDTO hotelDTO = mapToDTO(hotel);

        List<HotelDetailsDTO> details = offers.stream()
                .map(offer -> new HotelDetailsDTO(hotelDTO.withOffer(offer), offer))
                .toList();

        if (details.isEmpty()) {
            return List.of(new HotelDetailsDTO(hotelDTO, null));
        }

        return details;
    }

    public Price getLowestPriceForHotel(String hotelId, int adults, String checkInDate, String checkOutDate) {
        List<HotelOfferDTO> offers = hotelOfferService.getOffersByHotelId(hotelId, adults, checkInDate, checkOutDate);
        return offers.stream()
                .map(HotelOfferDTO::price)
                .filter(price -> price != null && price.getAmount() > 0)
                .min((p1, p2) -> Double.compare(p1.getAmount(), p2.getAmount()))
                .orElse(null);
    }

    public List<HotelDTO> getHotelsByCoordinates(Double latitude, Double longitude, String cityName, String countryCode) {

        List<Hotel> hotels = List.of();

        String normalizedCity = normalize(cityName);
        String normalizedCountry = normalize(countryCode);

        if (normalizedCity != null && normalizedCountry != null) {
            hotels = hotelRepository.findByAddress_CityNameIgnoreCaseAndAddress_CountryCodeIgnoreCase(normalizedCity, normalizedCountry);
        }

        if (hotels.isEmpty() && normalizedCity != null) {
            hotels = hotelRepository.findByAddress_CityNameIgnoreCase(normalizedCity);
        }

        if (hotels.isEmpty() && normalizedCountry != null) {
            hotels = hotelRepository.findByAddress_CountryCodeIgnoreCase(normalizedCountry);
        }

        if (hotels.isEmpty() && latitude != null && longitude != null) {
            hotels = hotelRepository.findByCoordinates_LatitudeAndCoordinates_Longitude(latitude, longitude);
        }

        if (!hotels.isEmpty()) {
            return hotels.stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        Logger.getLogger(HotelService.class.getName()).info("No hotels found in database for given destination.");

        return getHotelsByCoordinatesFromAPI(latitude, longitude, normalizedCity, normalizedCountry);

    }

    private List<HotelDTO> getHotelsByCoordinatesFromAPI(Double latitude, Double longitude, String cityName, String countryCode) {

        String url = String.format(Locale.US,
                "https://api.amadeus.com/v1/reference-data/locations/hotels/by-geocode?latitude=%f&longitude=%f&radius=2", latitude, longitude);

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

        return mapToDTOs(response, latitude, longitude, cityName, countryCode);
    }

    private List<HotelDTO> mapToDTOs(HotelResponse response, Double defaultLat, Double defaultLon, String defaultCity, String defaultCountry) {
        return response.getData().stream()
                .map(data -> {
                    GeoCode geoCode = resolveCoordinates(data, defaultLat, defaultLon);
                    HotelAddress address = mapHotelAddress(data.getAddress(), defaultCity, defaultCountry);
                    Hotel hotel = new Hotel(
                            data.getHotelId(),
                            data.getName(),
                            data.getIataCode(),
                            address,
                            geoCode
                    );
                    addHotel(hotel);
                    return mapToDTO(hotel);
                })
                .toList();
    }

    private HotelDTO mapToDTO(Hotel hotel) {

        return new HotelDTO(
                hotel.getHotelId(),
                hotel.getName(),
                hotel.getCoordinates(),
                hotel.getAddress(),
                List.of()
        );
    }

    private GeoCode resolveCoordinates(HotelResponse.HotelData data, Double defaultLat, Double defaultLon) {
        if (data.getGeoCode() != null) {
            return data.getGeoCode();
        }

        GeoCode enriched = fetchHotelCoordinates(data.getHotelId());
        if (enriched != null) {
            return enriched;
        }

        double lat = defaultLat != null ? defaultLat : 0;
        double lon = defaultLon != null ? defaultLon : 0;
        return new GeoCode(lat, lon);
    }

    private GeoCode fetchHotelCoordinates(String hotelId) {
        if (hotelId == null || hotelId.isBlank()) {
            return null;
        }
        String url = "https://api.amadeus.com/v1/reference-data/locations/hotels/" + hotelId;
        try {
            HotelDetailResponse response = webClientBuilder
                    .build()
                    .get()
                    .uri(url)
                    .header("Authorization", "Bearer " + amadeusAuthService.getAccessToken())
                    .retrieve()
                    .bodyToMono(HotelDetailResponse.class)
                    .block();
            if (response != null && response.data != null) {
                GeoCode code = response.data.getGeoCode();
                if (code != null) {
                    return code;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(HotelService.class.getName())
                    .warning("Unable to enrich coordinates for hotel " + hotelId + ": " + ex.getMessage());
        }
        return null;
    }

    private HotelAddress mapHotelAddress(HotelResponse.Address source, String defaultCity, String defaultCountry) {
        if (source == null) {
            return new HotelAddress(null, defaultCity, defaultCountry);
        }
        String firstLine = null;
        if (source.getLines() != null && !source.getLines().isEmpty()) {
            firstLine = source.getLines().get(0);
        }
        return new HotelAddress(
                firstLine,
                normalize(source.getCityName() != null ? source.getCityName() : defaultCity),
                normalize(source.getCountryCode() != null ? source.getCountryCode() : defaultCountry)
        );
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }

    private static class HotelDetailResponse {
        private HotelResponse.HotelData data;
    }
}


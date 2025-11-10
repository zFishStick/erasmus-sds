package com.sds2.service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.GeoCode;
import com.sds2.classes.response.HotelResponse;
import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelDetailsDTO;
import com.sds2.dto.HotelOfferDTO;
import com.sds2.dto.HotelBookingResult;
import com.sds2.dto.OffersPreview;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sds2.amadeus.dto.HotelModels.HotelSummary;
import com.sds2.repository.HotelRepository;

import com.sds2.classes.hotel.Hotel;
import com.sds2.classes.hotel.HotelOffer;

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
        hotelRepository.save(hotel);
    }

    public HotelDetailsDTO getHotelById(String hotelId, int adults) {
        HotelDTO hotel = hotelRepository.findByHotelId(hotelId);
        HotelOfferDTO offer = hotelOfferService.getOffersByHotelId(hotelId, adults);        
        return new HotelDetailsDTO(hotel, offer);
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
         "https://api.amadeus.com/v1/reference-data/locations/hotels/by-city?radius=2&cityCode=%s", iataCode);

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

    private List<HotelDTO> mapToDTOs(HotelResponse response, String iataCode, String cityName) {
        return response.getData().stream()
            .map(data -> {
                Hotel hotel = new Hotel(
                    data.getHotelId().toString(),
                    data.getName(),
                    data.getAddress(),
                    cityName,
                    iataCode,
                    data.getCoordinates()
                );
                addHotel(hotel);
                return mapToDTO(hotel);
            })
            .toList();
    }

    private HotelDTO mapToDTO(Hotel hotel) {
        return new HotelDTO(
            hotel.getName(),
            hotel.getCityName(),
            hotel.getCountryCode(),
            hotel.getAddress()
        );
    }

    // public OffersPreview previewPrices(List<String> hotelIds, String checkIn, String checkOut) {
    //     Map<String, String> priceMap = new HashMap<>();
    //     Set<String> checkedIds = new HashSet<>();
    //     if (hotelIds == null || hotelIds.isEmpty()) return new OffersPreview(priceMap, checkedIds);
    //     var container = amadeusAuthService.offersForHotelsRaw(hotelIds, checkIn, checkOut);
    //     if (container != null && container.data != null) {
    //         for (var item : container.data) {
    //             String hid = item.hotel != null ? item.hotel.hotelId : null;
    //             if (hid != null && !hid.isBlank()) checkedIds.add(hid);
    //             if (item.offers != null && !item.offers.isEmpty()) {
    //                 var first = item.offers.get(0);
    //                 String total = first.price != null ? first.price.total : null;
    //                 String cur = first.price != null ? first.price.currency : null;
    //                 if (total != null && !total.isBlank()) priceMap.put(hid, (cur == null || cur.isBlank()) ? total : total + " " + cur);
    //             }
    //         }
    //     }
    //     return new OffersPreview(priceMap, checkedIds);
    // }

    // public List<HotelOfferDTO> getOffers(String hotelId, String checkIn, String checkOut) {
    //     List<HotelOfferDTO> out = new ArrayList<>();
    //     var container = amadeusAuthService.offersForHotelsRaw(List.of(hotelId), checkIn, checkOut);
    //     if (container != null && container.data != null) {
    //         for (var item : container.data) {
    //             String hid = item.hotel != null ? item.hotel.hotelId : null;
    //             if (item.offers != null) for (var o : item.offers) {
    //                 out.add(new HotelOfferDTO(
    //                         hid,
    //                         nullSafe(o, () -> o.id),
    //                         nullSafe(o, () -> o.price.total),
    //                         nullSafe(o, () -> o.price.currency),
    //                         nullSafe(o, () -> o.room.type),
    //                         o.guests != null ? o.guests.adults : null,
    //                         o.boardType
    //                 ));
    //             }
    //         }
    //     }
    //     return out;
    // }

    // public HotelBookingResult book(String hotelOfferId, String firstName, String lastName, String email, String phone) {
    //     var order = amadeusAuthService.createHotelOrder(hotelOfferId, firstName, lastName, email, phone);
    //     if (order == null || order.data == null) return new HotelBookingResult(null, null, null, "No response");
    //     String oid = order.data.id;
    //     String bs = null;
    //     String cn = null;
    //     if (order.data.hotelBookings != null && !order.data.hotelBookings.isEmpty()) {
    //         var first = order.data.hotelBookings.get(0);
    //         bs = first.bookingStatus;
    //         if (first.hotelProviderInformation != null && !first.hotelProviderInformation.isEmpty()) {
    //             cn = first.hotelProviderInformation.get(0).confirmationNumber;
    //         }
    //     }
    //     return new HotelBookingResult(bs, cn, oid, null);
    // }

    // private static String nullSafe(Object root, Callable<String> supplier) {
    //     try { return supplier.call(); } catch (Exception e) { return null; }
    // }

    // private List<HotelDTO> mapEntities(List<Hotel> hotels) {
    //     List<HotelDTO> out = new ArrayList<>();
    //     for (Hotel h : hotels) {
    //         out.add(new HotelDTO(h.getHotelId(), h.getName(), h.getAddress(),
    //                 h.getCoordinates() != null ? h.getCoordinates().getLatitude() : null,
    //                 h.getCoordinates() != null ? h.getCoordinates().getLongitude() : null,
    //                 h.getRating(), h.getCityName(), h.getCountryCode()));
    //     }
    //     return out;
    // }

}

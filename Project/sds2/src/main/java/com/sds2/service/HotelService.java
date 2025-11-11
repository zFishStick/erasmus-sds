package com.sds2.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.response.HotelResponse;
import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelDetailsDTO;
import com.sds2.dto.HotelOfferDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sds2.repository.HotelRepository;

import com.sds2.classes.hotel.Hotel;

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

    public List<HotelDetailsDTO> getHotelById(String hotelId, int adults) {
        Logger.getLogger(HotelService.class.getName())
            .info("Fetching hotel details for ID: " + hotelId + " with " + adults + " adults.");

        Hotel hotel = hotelRepository.findByHotelId(hotelId);

        if (hotel == null) {
            Logger.getLogger(HotelService.class.getName()).info("No hotel found in database for ID: " + hotelId);
            return List.of();
        }

        List<HotelOfferDTO> offers = hotelOfferService.getOffersByHotelId(hotelId, adults);

        Logger.getLogger(HotelService.class.getName()).info("Fetched " + offers.size() + " offers for hotel ID: " + hotelId);
        
        HotelDTO hotelDTO = mapToDTO(hotel);

        return offers.stream()
            .map(offer -> new HotelDetailsDTO(hotelDTO.withOffer(offer), offer))
            .toList();

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

        return getHotelsByIataCodeFromAPI(iataCode);

    }

    private List<HotelDTO> getHotelsByIataCodeFromAPI(String iataCode) {

        String url = String.format(Locale.US,
         "https://api.amadeus.com/v1/reference-data/locations/hotels/by-city?cityCode=%s&radius=2", iataCode);

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
        
        return mapToDTOs(response);
    }

    private String convertToIataCode(String input) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("airports.json")) {
            if (is == null) {
                throw new FileNotFoundException("airports.json not found in resources");
            }
            Map<String, Map<String, Object>> airports = mapper.readValue(is, Map.class);

            for (Map<String, Object> airport : airports.values()) {
                if (input.equalsIgnoreCase((String) airport.get("city"))) {
                    String iata = (String) airport.get("iata");
                    if (iata != null && !iata.isEmpty()) {
                        return iata;
                    }
                }
            }
            return input;

        } catch (Exception e) {
            Logger.getLogger(HotelService.class.getName())
                .severe("Error reading airports.json: " + e.getMessage());
            return input;
        }
    }


    private List<HotelDTO> mapToDTOs(HotelResponse response) {
        return response.getData().stream()
            .map(data -> {
                Hotel hotel = new Hotel(
                    data.getHotelId(),
                    data.getName(),
                    data.getIataCode(),
                    data.getAddress(),
                    data.getCoordinates()
                );
                addHotel(hotel);
                return mapToDTO(hotel);
            })
            .toList();
    }

    private HotelDTO mapToDTO(Hotel hotel) {

        List<HotelOfferDTO> offersDTO = hotel.getOffers().stream()
            .map(offer -> new HotelOfferDTO(
                offer.getOfferId(),
                offer.getCheckInDate(),
                offer.getCheckOutDate(),
                offer.getPrice(),
                offer.getRoom(),
                offer.getAdults()
            ))
            .toList();

        return new HotelDTO(
            hotel.getHotelId(),
            hotel.getName(),
            hotel.getCoordinates(),
            hotel.getAddress(),
            offersDTO
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

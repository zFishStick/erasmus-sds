package com.sds2.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.sds2.classes.Price;
import com.sds2.classes.Room;
import com.sds2.classes.response.HotelOfferResponse;
import com.sds2.dto.HotelOfferDTO;

@Service
public class HotelOfferService {

    private final AmadeusAuthService amadeusAuthService;
    private final WebClient.Builder webClientBuilder;

    public HotelOfferService(
        AmadeusAuthService amadeusAuthService,
        WebClient.Builder webClientBuilder
    ) {
        this.amadeusAuthService = amadeusAuthService;
        this.webClientBuilder = webClientBuilder;
    }

    public List<HotelOfferDTO> getOffersByHotelId(String hotelId, int adultsNum, String checkInDate, String checkOutDate) {
        return getOffersByHotelAndAdultsByAPI(hotelId, adultsNum, checkInDate, checkOutDate);
    }

    private List<HotelOfferDTO> getOffersByHotelAndAdultsByAPI(String hotelId, int adultsNum, String checkInDate, String checkOutDate) {
        StringBuilder urlBuilder = new StringBuilder(
            String.format(Locale.US,
                "https://api.amadeus.com/v3/shopping/hotel-offers?hotelIds=%s&adults=%s", hotelId, adultsNum)
        );

        if (hasDates(checkInDate, checkOutDate)) {
            urlBuilder.append("&checkInDate=").append(checkInDate)
                .append("&checkOutDate=").append(checkOutDate);
        }

        String url = urlBuilder.toString();
        Logger.getLogger(HotelOfferService.class.getName()).info("URI for Hotel Offers API: " + url);

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            Logger.getLogger(HotelOfferService.class.getName()).severe("Invalid URI syntax: " + e.getMessage());
            return List.of();
        }

        HotelOfferResponse response;
        try {
            response = webClientBuilder
                .build()
                .get()
                .uri(uri)
                .header("Authorization", "Bearer " + amadeusAuthService.getAccessToken())
                .retrieve()
                .bodyToMono(HotelOfferResponse.class)
                .block();
        } catch (WebClientResponseException e) {
            Logger.getLogger(HotelOfferService.class.getName())
                .warning("Failed to fetch hotel offers from API: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            return List.of();
        }

        if (response == null || response.getData() == null) {
            throw new IllegalStateException("Failed to retrieve hotel offers from API: response data is null");
        }

        int fetchedOffers = response.getData().stream()
            .mapToInt(data -> data.getOffers() != null ? data.getOffers().size() : 0)
            .sum();

        Logger.getLogger(HotelOfferService.class.getName())
            .info("Fetched " + fetchedOffers + " offers from API for hotel ID: " + hotelId);

        return mapToDTOs(response, adultsNum, checkInDate, checkOutDate);
    }

    private List<HotelOfferDTO> mapToDTOs(HotelOfferResponse response, int adultsNum, String checkInDate, String checkOutDate) {
        return response.getData().stream()
            .filter(data -> data.getOffers() != null)
            .flatMap(data -> data.getOffers().stream())
            .map(offer -> {
                Room room = mapRoom(offer.getRoom());
                Price price = mapPrice(offer.getPrice());
                int resolvedAdults = resolveAdults(offer, adultsNum);

                String resolvedCheckIn = offer.getCheckInDate() != null ? offer.getCheckInDate() : checkInDate;
                String resolvedCheckOut = offer.getCheckOutDate() != null ? offer.getCheckOutDate() : checkOutDate;

                return new HotelOfferDTO(
                    offer.getId(),
                    resolvedCheckIn,
                    resolvedCheckOut,
                    price,
                    room,
                    resolvedAdults
                );
            })
            .toList();
    }

    private Room mapRoom(HotelOfferResponse.OfferRoom source) {
        if (source == null) {
            return null;
        }
        Room room = new Room();
        if (source.getTypeEstimated() != null && source.getTypeEstimated().getCategory() != null) {
            room.setCategory(source.getTypeEstimated().getCategory());
        } else {
            room.setCategory(source.getType());
        }
        if (source.getDescription() != null) {
            room.setDescription(source.getDescription().getText());
        }
        return room;
    }

    private Price mapPrice(HotelOfferResponse.OfferPrice source) {
        if (source == null) {
            return null;
        }
        double amount = 0;
        if (source.getTotal() != null) {
            try {
                amount = Double.parseDouble(source.getTotal());
            } catch (NumberFormatException ex) {
                Logger.getLogger(HotelOfferService.class.getName()).warning("Unable to parse price total: " + source.getTotal());
            }
        }
        return new Price(amount, source.getCurrency());
    }

    private int resolveAdults(HotelOfferResponse.OfferItem offer, int defaultAdults) {
        return offer.getGuests() != null && offer.getGuests().getAdults() != null
            ? offer.getGuests().getAdults()
            : defaultAdults;
    }

    private boolean hasDates(String checkInDate, String checkOutDate) {
        return checkInDate != null && !checkInDate.isBlank()
            && checkOutDate != null && !checkOutDate.isBlank();
    }
}

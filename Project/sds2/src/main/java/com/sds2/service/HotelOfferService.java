package com.sds2.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.hotel.HotelOffer;
import com.sds2.classes.response.HotelOfferResponse;
import com.sds2.classes.response.HotelResponse;
import com.sds2.dto.HotelOfferDTO;
import com.sds2.repository.HotelOfferRepository;

@Service
public class HotelOfferService {
    private final HotelOfferRepository hotelOfferRepository;
    private final HotelService hotelService;
    private final AmadeusAuthService amadeusAuthService;
    private final WebClient.Builder webClientBuilder;

    public HotelOfferService(
        HotelOfferRepository hotelOfferRepository,
        HotelService hotelService,
        AmadeusAuthService amadeusAuthService,
        WebClient.Builder webClientBuilder
    ) {
        this.hotelOfferRepository = hotelOfferRepository;
        this.hotelService = hotelService;
        this.amadeusAuthService = amadeusAuthService;
        this.webClientBuilder = webClientBuilder;
    }

    public void addHotelOffer(HotelOffer offer) {
        if (offer == null) {
            throw new IllegalArgumentException("Hotel offer cannot be null");
        }
        hotelOfferRepository.save(offer);
    }

    public HotelOfferDTO getOffersByHotelId(String hotelId, int adultsNum) {
        HotelOffer hotelOffer = hotelOfferRepository.findByHotelIdAndAdults(hotelId, adultsNum);

        if (hotelOffer != null) {
            return mapToDTO(hotelOffer);
        }

        return getOffersByHotelAndAdultsByAPI(hotelId, adultsNum);

    }

    public HotelOfferDTO getOffersByHotelAndAdultsByAPI(String hotelId, int adultsNum) {
        
        String url = String.format(Locale.US,
        "https://api.amadeus.com/v3/shopping/hotel-offers?hotelIds=%s&adults=%s", hotelId, adultsNum);

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            Logger.getLogger(HotelService.class.getName()).severe("Invalid URI syntax: " + e.getMessage());
            return null;
        }

        HotelOfferResponse response = webClientBuilder
            .build()
            .get()
            .uri(uri)
            .header("Authorization", "Bearer " + amadeusAuthService.getAccessToken())
            .retrieve()
            .bodyToMono(HotelOfferResponse.class)
            .block();

        if (response == null || response.getData() == null) {
            throw new IllegalStateException("Failed to retrieve hotel offers from API: response data is null");
        }

        return mapToDTOs(response, hotelId, adultsNum);

    }

    public HotelOfferDTO mapToDTOs(HotelOfferResponse response, String hotelId, int adultsNum) {
        return response.getData().stream().findFirst()
        .map(data -> {
            HotelOffer hotelOffer = new HotelOffer(
                data.getOffers().getId(),
                hotelId,
                data.getOffers().getCheckInDate(),
                data.getOffers().getCheckOutDate(),
                data.getRoom(),
                data.getPrice(),
                adultsNum
            );
            addHotelOffer(hotelOffer);
            return mapToDTO(hotelOffer);
        }).orElse(null);
    }

    private HotelOfferDTO mapToDTO(HotelOffer hotelOffer) {
        return new HotelOfferDTO(
            hotelOffer.getHotelId(),
            hotelOffer.getOfferId(),
            hotelOffer.getCheckInDate(),
            hotelOffer.getCheckOutDate(),
            hotelOffer.getPrice(),
            hotelOffer.getRoom(),
            hotelOffer.getAdults()
        );
    }

}


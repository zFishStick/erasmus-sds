package com.sds2.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.hotel.Hotel;
import com.sds2.classes.hotel.HotelOffer;
import com.sds2.classes.response.HotelOfferResponse;
import com.sds2.dto.HotelOfferDTO;
import com.sds2.repository.HotelOfferRepository;
import com.sds2.repository.HotelRepository;

@Service
public class HotelOfferService {
    private final HotelOfferRepository hotelOfferRepository;
    private final AmadeusAuthService amadeusAuthService;
    private final WebClient.Builder webClientBuilder;
    private final HotelRepository hotelRepository;

    public HotelOfferService(
        HotelOfferRepository hotelOfferRepository,
        AmadeusAuthService amadeusAuthService,
        WebClient.Builder webClientBuilder,
        HotelRepository hotelRepository
    ) {
        this.hotelOfferRepository = hotelOfferRepository;
        this.amadeusAuthService = amadeusAuthService;
        this.webClientBuilder = webClientBuilder;
        this.hotelRepository = hotelRepository;
    }

    public void addHotelOffer(HotelOffer offer) {
        if (offer == null) {
            throw new IllegalArgumentException("Hotel offer cannot be null");
        }
        hotelOfferRepository.save(offer);
    }

    public List<HotelOfferDTO> getOffersByHotelId(String hotelId, int adultsNum) {

        Hotel hotel = hotelRepository.findByHotelId(hotelId);
        List<HotelOffer> hotelOffer = hotelOfferRepository.findByHotelAndAdults(hotel, adultsNum);

        if (hotelOffer != null) {
            return hotelOffer.stream()
                .map(this::mapToDTO)
                .toList();
        }

        return getOffersByHotelAndAdultsByAPI(hotel, adultsNum);

    }

    public List<HotelOfferDTO> getOffersByHotelAndAdultsByAPI(Hotel hotel, int adultsNum) {
        
        String url = String.format(Locale.US,
        "https://api.amadeus.com/v3/shopping/hotel-offers?hotelIds=%s&adults=%s", hotel.getHotelId(), adultsNum);

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            Logger.getLogger(HotelService.class.getName()).severe("Invalid URI syntax: " + e.getMessage());
            return List.of();
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

        return mapToDTOs(response, hotel, adultsNum);

    }

    public List<HotelOfferDTO> mapToDTOs(HotelOfferResponse response, Hotel hotel, int adultsNum) {
        return response.getData().stream()
        .map(data -> {
            HotelOffer hotelOffer = new HotelOffer(
                data.getOffers().getId(),
                data.getOffers().getCheckInDate(),
                data.getOffers().getCheckOutDate(),
                data.getRoom(),
                data.getPrice(),
                adultsNum,
                hotel
            );
            addHotelOffer(hotelOffer);
            return mapToDTO(hotelOffer);
        }).toList();
    }

    private HotelOfferDTO mapToDTO(HotelOffer hotelOffer) {
        return new HotelOfferDTO(
            hotelOffer.getOfferId(),
            hotelOffer.getCheckInDate(),
            hotelOffer.getCheckOutDate(),
            hotelOffer.getPrice(),
            hotelOffer.getRoom(),
            hotelOffer.getAdults()
        );
    }

}


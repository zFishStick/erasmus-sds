package com.sds2.dto;

public record HotelOfferDTO(
        String hotelId,
        String offerId,
        String total,
        String currency,
        String roomType,
        Integer adults,
        String boardType
) {}


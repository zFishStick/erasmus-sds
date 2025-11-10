package com.sds2.dto;

public record HotelDTO(
        String hotelId,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String rating,
        String cityName,
        String countryCode
) {}

package com.sds2.classes.hotel;

public record HotelAvailability (
        String status,
        Double amount,
        String currency
) {}


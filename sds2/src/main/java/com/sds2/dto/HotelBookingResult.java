package com.sds2.dto;

public record HotelBookingResult(
        String status,
        String confirmationNumber,
        String orderId,
        String error
) {
    public boolean isSuccess() { return error == null || error.isBlank(); }
}


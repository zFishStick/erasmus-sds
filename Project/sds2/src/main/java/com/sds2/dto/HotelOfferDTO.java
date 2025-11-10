package com.sds2.dto;

import com.sds2.classes.Price;
import com.sds2.classes.Room;

public record HotelOfferDTO(
        String hotelId,
        String offerId,
        String checkInDate,
        String checkOutDate,
        Price price,
        Room room,
        int adults
) {}


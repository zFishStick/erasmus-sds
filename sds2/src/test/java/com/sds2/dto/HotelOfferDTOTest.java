package com.sds2.dto;

import org.junit.jupiter.api.Test;

import com.sds2.classes.Price;
import com.sds2.classes.Room;

public class HotelOfferDTOTest {
    @Test
    public void testHotelOfferDTO(){
        HotelOfferDTO hotelOfferDTO = getHotelOfferDTOTest();
    }

    public HotelOfferDTO getHotelOfferDTOTest(){
        String offerId = "hello";
        String checkInDate = "hello";
        String checkOutDate = "hello";
        Price price = new Price(1.2, "PLN");
        Room room = new Room();
        room.setCategory("hello");
        room.setDescription("hello");
        int adults = 0;
        return new HotelOfferDTO(offerId, checkInDate, checkOutDate, price, room, adults);
    }
}

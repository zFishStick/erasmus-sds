package com.sds2.dto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.sds2.classes.Price;
import com.sds2.classes.Room;
import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.hotel.HotelAddress;

class HotelDTOTest {

    @Test
    void recordHoldsValues() {
        HotelDTO dto = getExampleHotelDTO();
        
        assertEquals("H1", dto.hotelId());
        assertEquals("Hotel", dto.name());
        assertEquals(48.85, dto.coordinates().getLatitude());
        assertEquals("FR", dto.address().getCountryCode());
        assertTrue(dto.offers().isEmpty());
    }

    @Test
    void testWithOffer(){
        HotelDTO dto = getExampleHotelDTO();
        HotelOfferDTO hotelOfferDTO = getHotelOfferDTOTest();
        dto.withOffer(hotelOfferDTO);
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

    public HotelDTO getExampleHotelDTO(){
        HotelAddress address = new HotelAddress();
        address.setCountryCode("FR");
        address.setCityName("PARIS");
        address.setLine("1 Rue de Paris");
        GeoCode coords = new GeoCode(48.85, 2.35);
        List<HotelOfferDTO> offers = new ArrayList<>();

        HotelDTO dto = new HotelDTO("H1", "Hotel", coords, address, offers);
        return dto;
    }
}

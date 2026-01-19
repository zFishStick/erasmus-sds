package com.sds2.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sds2.classes.Price;
import com.sds2.classes.Room;
import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.hotel.Hotel;
import com.sds2.classes.hotel.HotelAddress;
import com.sds2.classes.response.HotelResponse;
import com.sds2.classes.response.HotelResponse.Address;
import com.sds2.classes.response.HotelResponse.HotelData;
import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelOfferDTO;
import com.sds2.repository.HotelRepository;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelOfferService hotelOfferService;

    @Mock AmadeusAPICall amadeusAPICall;

    @InjectMocks
    private HotelService hotelService;

    @Test
    void testGetHotelsByCoordinates() {

        when(hotelRepository.findByAddress_CityNameIgnoreCaseAndAddress_CountryCodeIgnoreCase(any(), any()))
                .thenReturn(List.of());
        when(hotelRepository.findByAddress_CityNameIgnoreCase(any()))
                .thenReturn(List.of());
        when(hotelRepository.findByAddress_CountryCodeIgnoreCase(any()))
                .thenReturn(List.of());
        when(hotelRepository.findByCoordinates_LatitudeAndCoordinates_Longitude(anyDouble(), anyDouble()))
                .thenReturn(List.of());

        HotelResponse customResponse = getExampleHotelResponse();

        when(amadeusAPICall.getAPIResponse(argThat(obj, any())).thenReturn(customResponse);

        List<HotelDTO> result =
                hotelService.getHotelsByCoordinates(48.85, 2.35, "Paris", "FR");

        assertEquals(customResponse.getData().size(), result.size());
    }

    @Test
    void testGetHotelById() {
        HotelAddress hotelAddress = new HotelAddress("address", "city_name", "country_code");
        GeoCode geocode = new GeoCode(0D, 0D);
        when(hotelRepository.findByHotelId(any())).thenReturn(new Hotel(0L, "hotelId", "name", "iataCode", hotelAddress, geocode));

        HotelOfferDTO hotelOfferDTO = new HotelOfferDTO("OfferId", "CheckInDate", "CheckOutDate", new Price(0D, "EUR"), new Room("category", "description"), 1);
        when(hotelOfferService.getOffersByHotelId(any(), anyInt(), any(), any())).thenReturn(List.of(hotelOfferDTO));
        hotelService.getHotelById("hotelId", 1, "checkInDate", "checkOutDate");
    }

    @Test
    void testGetLowestPriceForHotel() {
        HotelOfferDTO hotelOfferDTO = new HotelOfferDTO("OfferId", "CheckInDate", "CheckOutDate", new Price(0D, "EUR"), new Room("category", "description"), 1);
        when(hotelOfferService.getOffersByHotelId(any(), anyInt(), any(), any())).thenReturn(List.of(hotelOfferDTO));

        hotelService.getLowestPriceForHotel("hotelId", 1, "CheckInDate", "CheckOutDate");

    }
    
    HotelResponse getExampleHotelResponse(){
            Address address = new Address("FRA", "Mulhouse", List.of("lines"));
            HotelData hotelData = new HotelData("name", "hotelId", "iataCode", null, address);

            return new HotelResponse(List.of(hotelData));
        }
}

package com.sds2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;

import com.sds2.classes.Price;
import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.entity.Hotel;
import com.sds2.classes.hotel.HotelAddress;
import com.sds2.classes.response.HotelResponse;
import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelDetailsDTO;
import com.sds2.dto.HotelOfferDTO;
import com.sds2.repository.HotelRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelOfferService hotelOfferService;

    @Mock
    private AmadeusAuthService amadeusAuthService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec headersSpec;


    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private HotelService hotelService;

    @Test
    void addHotel_null_throwsException() {
        assertThrows(IllegalArgumentException.class,
            () -> hotelService.addHotel(null));
    }

    @Test
    void addHotel_existingHotel_notSaved() {
        Hotel hotel = new Hotel("H1", "Hotel", null, null, null);
        when(hotelRepository.findByHotelId("H1")).thenReturn(hotel);

        hotelService.addHotel(hotel);

        verify(hotelRepository, never()).save(any());
    }

    @Test
    void addHotel_newHotel_saved() {
        Hotel hotel = new Hotel("H2", "Hotel", null, null, null);
        when(hotelRepository.findByHotelId("H2")).thenReturn(null);

        hotelService.addHotel(hotel);

        verify(hotelRepository).save(hotel);
    }

    @Test
    void getHotelById_notFound_returnsEmptyList() {
        when(hotelRepository.findByHotelId("X")).thenReturn(null);

        List<HotelDetailsDTO> res =
            hotelService.getHotelById("X", 2, "2024-01-01", "2024-01-02");

        assertTrue(res.isEmpty());
    }

    @Test
    void getHotelById_withOffers_returnsDetails() {
        Hotel hotel = new Hotel("H1", "Hotel", null, null, null);
        when(hotelRepository.findByHotelId("H1")).thenReturn(hotel);

        HotelOfferDTO offer = mock(HotelOfferDTO.class);
        when(hotelOfferService.getOffersByHotelId(any(), anyInt(), any(), any()))
            .thenReturn(List.of(offer));

        List<HotelDetailsDTO> res =
            hotelService.getHotelById("H1", 2, "2024-01-01", "2024-01-02");

        assertEquals(1, res.size());
        assertNotNull(res.get(0).offer());
    }

    @Test
    void getLowestPriceForHotel_returnsLowest() {
        Price p1 = new Price(100, "EUR");
        Price p2 = new Price(80, "EUR");

        HotelOfferDTO o1 = mock(HotelOfferDTO.class);
        HotelOfferDTO o2 = mock(HotelOfferDTO.class);

        when(o1.price()).thenReturn(p1);
        when(o2.price()).thenReturn(p2);

        when(hotelOfferService.getOffersByHotelId(any(), anyInt(), any(), any()))
            .thenReturn(List.of(o1, o2));

        Price res = hotelService.getLowestPriceForHotel("H1", 2, "in", "out");

        assertEquals(80, res.getAmount());
    }

    @Test
    void getHotelsByCoordinates_foundInDb_returnsDtos() {
        Hotel hotel = new Hotel(
            "H1",
            "Hotel",
            null,
            new HotelAddress("addr", "Rome", "IT"),
            new GeoCode(1, 1)
        );

        when(hotelRepository
            .findByAddress_CityNameIgnoreCaseAndAddress_CountryCodeIgnoreCase("Rome", "IT"))
            .thenReturn(List.of(hotel));

        List<HotelDTO> res =
            hotelService.getHotelsByCoordinates(1.0, 1.0, "Rome", "IT");

        assertEquals(1, res.size());
        verify(webClientBuilder, never()).build();
    }

    @Test
    void getHotelsByCoordinates_notInDb_callsApi() {
        when(hotelRepository.findByAddress_CityNameIgnoreCaseAndAddress_CountryCodeIgnoreCase(any(), any()))
            .thenReturn(List.of());
        when(hotelRepository.findByAddress_CityNameIgnoreCase(any()))
            .thenReturn(List.of());
        when(hotelRepository.findByAddress_CountryCodeIgnoreCase(any()))
            .thenReturn(List.of());
        when(hotelRepository.findByCoordinates_LatitudeAndCoordinates_Longitude(anyDouble(), anyDouble()))
            .thenReturn(List.of());

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.header(anyString(), anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(amadeusAuthService.getAccessToken()).thenReturn("token");

        HotelResponse.HotelData data = new HotelResponse.HotelData();
        data.setHotelId("H1");
        data.setName("Hotel");

        HotelResponse response = new HotelResponse();
        response.setData(List.of(data));

        when(responseSpec.bodyToMono(HotelResponse.class))
            .thenReturn(Mono.just(response));

        List<HotelDTO> res = hotelService.getHotelsByCoordinates(1.0, 1.0, "Rome", "IT");

        assertEquals(1, res.size());
        verify(hotelRepository).save(any());
    }

}

package com.sds2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.sds2.classes.response.HotelOfferResponse;
import com.sds2.classes.response.HotelOfferResponse.HotelOfferData;
import com.sds2.classes.response.HotelOfferResponse.OfferGuests;
import com.sds2.classes.response.HotelOfferResponse.OfferItem;
import com.sds2.classes.response.HotelOfferResponse.OfferPrice;
import com.sds2.classes.response.HotelOfferResponse.OfferRoom;
import com.sds2.classes.response.HotelOfferResponse.OfferRoomDescription;
import com.sds2.classes.response.HotelOfferResponse.OfferRoomTypeEstimated;
import com.sds2.dto.HotelOfferDTO;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class HotelOfferServiceTest {

    @Mock
    private AmadeusAuthService amadeusAuthService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private HotelOfferService hotelOfferService;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(amadeusAuthService.getAccessToken()).thenReturn("fake-token");
    }

    @Test
    void getOffersByHotelId_SuccessfulResponse_WithDates() {
        HotelOfferResponse mockResponse = createMockResponse("120.50", "EUR", "Standard Room");
        when(responseSpec.bodyToMono(HotelOfferResponse.class)).thenReturn(Mono.just(mockResponse));

        List<HotelOfferDTO> result = hotelOfferService.getOffersByHotelId("HOTEL123", 2, "2023-10-01", "2023-10-05");

        assertNotNull(result);
        assertEquals(1, result.size());
        HotelOfferDTO dto = result.get(0);
        assertEquals(120.50, dto.price().getAmount());
        assertEquals("EUR", dto.price().getCurrencyCode());
        assertEquals("Standard Room", dto.room().getCategory());
        assertEquals(2, dto.adults());
        assertEquals("offer-1", dto.offerId());
    }

    @Test
    void getOffersByHotelId_SuccessfulResponse_WithoutDates() {
        HotelOfferResponse mockResponse = createMockResponse("200.00", "USD", "Suite");
        when(responseSpec.bodyToMono(HotelOfferResponse.class)).thenReturn(Mono.just(mockResponse));

        List<HotelOfferDTO> result = hotelOfferService.getOffersByHotelId("HOTEL123", 1, null, "");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(200.00, result.get(0).price().getAmount());
    }

    @Test
    void getOffersByHotelId_WebClientException_ReturnsEmptyList() {
        when(responseSpec.bodyToMono(HotelOfferResponse.class)).thenThrow(new WebClientResponseException(404, "Not Found", null, null, null));

        List<HotelOfferDTO> result = hotelOfferService.getOffersByHotelId("HOTEL123", 2, "2023-10-01", "2023-10-05");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOffersByHotelId_NullResponseData_ThrowsException() {
        HotelOfferResponse mockResponse = new HotelOfferResponse();
        mockResponse.setData(null);
        when(responseSpec.bodyToMono(HotelOfferResponse.class)).thenReturn(Mono.just(mockResponse));

        assertThrows(IllegalStateException.class, () -> 
            hotelOfferService.getOffersByHotelId("HOTEL123", 2, "2023-10-01", "2023-10-05")
        );
    }

    @Test
    void getOffersByHotelId_NullResponse_ThrowsException() {
        when(responseSpec.bodyToMono(HotelOfferResponse.class)).thenReturn(Mono.empty());

        assertThrows(IllegalStateException.class, () -> 
            hotelOfferService.getOffersByHotelId("HOTEL123", 2, "2023-10-01", "2023-10-05")
        );
    }

    @Test
    void getOffersByHotelId_InvalidURI_ReturnsEmptyList() {
        String invalidHotelId = "INVALID ID WITH SPACES ^"; 
        
        List<HotelOfferDTO> result = hotelOfferService.getOffersByHotelId(invalidHotelId, 2, "2023-10-01", "2023-10-05");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(webClientBuilder, never()).build();
    }

    @Test
    void getOffersByHotelId_InvalidPriceFormat_HandlesGracefully() {
        HotelOfferResponse mockResponse = createMockResponse("NOT_A_NUMBER", "EUR", "Room");
        when(responseSpec.bodyToMono(HotelOfferResponse.class)).thenReturn(Mono.just(mockResponse));

        List<HotelOfferDTO> result = hotelOfferService.getOffersByHotelId("HOTEL123", 2, "2023-10-01", "2023-10-05");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0.0, result.get(0).price().getAmount());
    }

    @Test
    void getOffersByHotelId_RoomDescriptionMapping() {
        HotelOfferResponse mockResponse = createMockResponse("100", "USD", "Room");
        mockResponse.getData().get(0).getOffers().get(0).getRoom().getTypeEstimated().setCategory(null);
        mockResponse.getData().get(0).getOffers().get(0).getRoom().setType("FallbackType");
        
        OfferRoomDescription desc = new OfferRoomDescription();
        desc.setText("Nice view");
        mockResponse.getData().get(0).getOffers().get(0).getRoom().setDescription(desc);

        when(responseSpec.bodyToMono(HotelOfferResponse.class)).thenReturn(Mono.just(mockResponse));

        List<HotelOfferDTO> result = hotelOfferService.getOffersByHotelId("HOTEL123", 2, null, null);

        assertEquals("FallbackType", result.get(0).room().getCategory());
        assertEquals("Nice view", result.get(0).room().getDescription());
    }

    private HotelOfferResponse createMockResponse(String priceTotal, String currency, String roomCategory) {
        HotelOfferResponse response = new HotelOfferResponse();
        HotelOfferData data = new HotelOfferData();
        OfferItem offer = new OfferItem();
        offer.setId("offer-1");
        
        OfferPrice price = new OfferPrice();
        price.setTotal(priceTotal);
        price.setCurrency(currency);
        offer.setPrice(price);

        OfferRoom room = new OfferRoom();
        OfferRoomTypeEstimated type = new OfferRoomTypeEstimated();
        type.setCategory(roomCategory);
        room.setTypeEstimated(type);
        offer.setRoom(room);

        OfferGuests guests = new OfferGuests();
        guests.setAdults(2);
        offer.setGuests(guests);

        data.setOffers(List.of(offer));
        response.setData(List.of(data));
        return response;
    }
}
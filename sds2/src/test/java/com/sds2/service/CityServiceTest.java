package com.sds2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.entity.City;
import com.sds2.classes.response.CityResponse;
import com.sds2.dto.CityDTO;
import com.sds2.repository.CityRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    CityRepository cityRepository;

    @Mock
    AmadeusAuthService amadeusAuthService;

    @Mock
    WebClient.Builder webClientBuilder;

    @Mock
    WebClient webClient;

    @Mock
    WebClient.ResponseSpec responseSpec;

    CityService cityService;

    @BeforeEach
    void setUp() {
        cityService = new CityService(cityRepository, amadeusAuthService, webClientBuilder);
    }

    @Test
    void getCity_foundInDb_returnsDto() throws IOException {
        City city = new City("Rome", "IT", 41.9, 12.5);
        when(cityRepository.findByNameStartingWithIgnoreCase("Ro")).thenReturn(List.of(city));

        List<CityDTO> result = cityService.getCity("Ro");

        assertEquals(1, result.size());
        assertEquals("Rome", result.get(0).name());
        assertEquals("IT", result.get(0).country());

        verifyNoInteractions(webClientBuilder);
    }

    @Test
    void getCity_notInDb_callsApi_andSaves() throws IOException {
        when(cityRepository.findByNameStartingWithIgnoreCase("Paris")).thenReturn(List.of());
        when(amadeusAuthService.getAccessToken()).thenReturn("token");

        WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);

        doReturn(webClient).when(webClientBuilder).build();
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(any(URI.class));
        doReturn(headersSpec).when(headersSpec).header(anyString(), anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();

        CityResponse response = new CityResponse();
        CityResponse.CityData data = new CityResponse.CityData();
        data.setName("Paris");
        data.setGeoCode(new com.sds2.classes.coordinates.GeoCode(48.8566, 2.3522));
        data.setAddress(new com.sds2.classes.response.CityResponse.Address("FR"));
        response.setData(List.of(data));

        when(responseSpec.bodyToMono(CityResponse.class)).thenReturn(Mono.just(response));
        when(cityRepository.save(any(City.class))).thenAnswer(inv -> inv.getArgument(0));

        List<CityDTO> result = cityService.getCity("Paris");

        assertEquals(1, result.size());
        assertEquals("Paris", result.get(0).name());
        assertEquals("FR", result.get(0).country());

        verify(cityRepository).save(any(City.class));
    }


    @Test
    void getCity_apiReturnsNull_throwsIOException() {
        when(cityRepository.findByNameStartingWithIgnoreCase("X")).thenReturn(List.of());
        when(amadeusAuthService.getAccessToken()).thenReturn("token");

        WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);

        doReturn(webClient).when(webClientBuilder).build();
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(any(URI.class));
        doReturn(headersSpec).when(headersSpec).header(anyString(), anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        

        when(responseSpec.bodyToMono(CityResponse.class)).thenReturn(Mono.empty());

        assertThrows(IOException.class, () -> cityService.getCity("X"));
    }
}

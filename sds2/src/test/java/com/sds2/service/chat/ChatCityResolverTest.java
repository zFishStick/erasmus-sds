package com.sds2.service.chat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sds2.dto.ChatItineraryRequest;
import com.sds2.dto.CityDTO;
import com.sds2.service.CityService;

@ExtendWith(MockitoExtension.class)
class ChatCityResolverTest {

    @Mock
    private CityService cityService;

    @InjectMocks
    private ChatCityResolver chatCityResolver;

    @Test
    void testResolve_WhenCoordinatesArePresent_ShouldReturnContextDirectly() throws IOException {
        // Arrange
        String destination = "Roma";
        String country = "IT";
        Double lat = 41.9028;
        Double lon = 12.4964;

        ChatItineraryRequest request = mock(ChatItineraryRequest.class);
        when(request.latitude()).thenReturn(lat);
        when(request.longitude()).thenReturn(lon);
        when(request.destination()).thenReturn(destination);
        when(request.countryCode()).thenReturn(country);

        // Act
        CityContext result = chatCityResolver.resolve(request);

        // Assert
        assertNotNull(result);
        assertEquals(destination, result.destination());
        assertEquals(country, result.country());
        assertEquals(lat, result.latitude());
        assertEquals(lon, result.longitude());

        verify(cityService, never()).getCity(anyString());
    }

    @Test
    void testResolve_WhenCoordinatesMissing_ShouldCallServiceAndUseRequestCountry() throws IOException {
        // Arrange
        String destination = "Milano";
        String requestCountry = "IT";
        Double lat = 45.4642;
        Double lon = 9.1900;

        ChatItineraryRequest request = mock(ChatItineraryRequest.class);
        when(request.latitude()).thenReturn(null);
        when(request.longitude()).thenReturn(null);
        when(request.destination()).thenReturn(" " + destination + " ");
        when(request.countryCode()).thenReturn(requestCountry);

        CityDTO cityDTO = mock(CityDTO.class);
        when(cityDTO.latitude()).thenReturn(lat);
        when(cityDTO.longitude()).thenReturn(lon);

        when(cityService.getCity(destination)).thenReturn(List.of(cityDTO));

        // Act
        CityContext result = chatCityResolver.resolve(request);

        // Assert
        assertEquals(destination, result.destination());
        assertEquals(requestCountry, result.country());
        assertEquals(lat, result.latitude());
        
        verify(cityService).getCity(destination);
    }

    @Test
    void testResolve_WhenCountryMissing_ShouldUseCityCountry() throws IOException {
        // Arrange
        String destination = "Parigi";
        String cityCountry = "FR";

        ChatItineraryRequest request = mock(ChatItineraryRequest.class);
        when(request.latitude()).thenReturn(null);
        when(request.longitude()).thenReturn(null);
        when(request.destination()).thenReturn(destination);
        when(request.countryCode()).thenReturn(null); // Country null

        CityDTO cityDTO = mock(CityDTO.class);
        when(cityDTO.latitude()).thenReturn(48.8566);
        when(cityDTO.longitude()).thenReturn(2.3522);
        when(cityDTO.country()).thenReturn(cityCountry);

        when(cityService.getCity(destination)).thenReturn(List.of(cityDTO));

        // Act
        CityContext result = chatCityResolver.resolve(request);

        // Assert
        assertEquals(cityCountry, result.country());
    }

    @Test
    void testResolve_WhenCityNotFound_ShouldThrowIllegalArgumentException() throws IOException {
        String destination = "Atlantide";
        ChatItineraryRequest request = mock(ChatItineraryRequest.class);
        when(request.latitude()).thenReturn(null);
        when(request.longitude()).thenReturn(null);
        when(request.destination()).thenReturn(destination);
        when(request.countryCode()).thenReturn("XX");

        when(cityService.getCity(destination)).thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            chatCityResolver.resolve(request);
        });

        assertEquals("Destination not found.", exception.getMessage());
    }

    @Test
    void testResolve_WhenServiceFails_ShouldThrowIllegalStateException() throws IOException {
        // Arrange
        String destination = "Errore";
        ChatItineraryRequest request = mock(ChatItineraryRequest.class);
        when(request.latitude()).thenReturn(null);
        when(request.longitude()).thenReturn(null);
        when(request.destination()).thenReturn(destination);
        when(request.countryCode()).thenReturn("XX");

        when(cityService.getCity(destination)).thenThrow(new IOException("Network error"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            chatCityResolver.resolve(request);
        });

        assertEquals("Failed to resolve destination coordinates.", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }
}
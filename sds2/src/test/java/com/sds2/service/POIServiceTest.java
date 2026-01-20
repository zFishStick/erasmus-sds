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
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.entity.POI;
import com.sds2.classes.poi.POIInfo;
import com.sds2.dto.POIDTO;
import com.sds2.repository.POIRepository;

@ExtendWith(MockitoExtension.class)
class POIServiceTest {

    @Mock
    private POIRepository poiRepository;

    @Mock
    private AmadeusAuthService amadeusAuthService;

    // --- Mocks per la catena WebClient (Tutti necessari) ---
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.Builder mutatedBuilder;
    @Mock
    private WebClient mutatedWebClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private POIService poiService;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.mutate()).thenReturn(mutatedBuilder);
        lenient().when(mutatedBuilder.exchangeStrategies(any(ExchangeStrategies.class))).thenReturn(mutatedBuilder);
        lenient().when(mutatedBuilder.build()).thenReturn(mutatedWebClient);
        lenient().when(mutatedWebClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void testAddPOI_ValidPOI_ShouldSave() {
        POI poi = new POI();
        poiService.addPOI(poi);
        verify(poiRepository).save(poi);
    }

    @Test
    void testAddPOI_NullPOI_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> poiService.addPOI(null));
        verify(poiRepository, never()).save(any());
    }

    @Test
    void testGetPOIById_ShouldReturnPOI() {
        long id = 1L;
        POI expected = new POI();
        when(poiRepository.findById(id)).thenReturn(expected);

        POI result = poiService.getPOIById(id);
        assertEquals(expected, result);
    }

    @Test
    void testGetPointOfInterests_WhenInDb_ShouldReturnFromDb() {
        GeoCode coords = new GeoCode(10.0, 10.0);
        String city = "Paris";
        String country = "FR";
        
        POI poi = createMockPOI(city, country, "Eiffel Tower");
        when(poiRepository.findByCityNameAndCountryCode(city, country)).thenReturn(List.of(poi));

        List<POIDTO> result = poiService.getPointOfInterests(coords, city, country);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Eiffel Tower", result.get(0).name());
        // Verifica che l'API non venga chiamata
        verify(webClientBuilder, never()).build();
    }

    // Helper methods
    private POI createMockPOI(String city, String country, String name) {
        POIInfo info = new POIInfo(name, "Sightseeing", "Desc", "url", "1h", "link");
        return new POI(city, country, info, null, new GeoCode(0.0, 0.0));
    }
    
}
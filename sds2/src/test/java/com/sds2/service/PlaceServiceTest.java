package com.sds2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.entity.Places;
import com.sds2.classes.response.PhotoResponse;
import com.sds2.classes.response.PlaceResponse;
import com.sds2.classes.response.PlaceResponse.DisplayName;
import com.sds2.classes.response.PlaceResponse.Photo;
import com.sds2.classes.response.PlaceResponse.PlacesData;
import com.sds2.dto.PlacesDTO;
import com.sds2.repository.PlacesRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlacesRepository placesRepository;

    @Mock
    private GoogleAuthService googleAuthService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PlaceService placeService;

    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void testAddPlace_WhenNewPlace_ShouldSave() {
        Places place = new Places();
        place.setName("places/123");

        when(placesRepository.findByName("places/123")).thenReturn(null);

        placeService.addPlace(place);

        verify(placesRepository).save(place);
    }

    @Test
    void testAddPlace_WhenExistingPlace_ShouldNotSave() {
        Places place = new Places();
        place.setName("places/existing");

        when(placesRepository.findByName("places/existing")).thenReturn(new Places());

        placeService.addPlace(place);

        verify(placesRepository, never()).save(any());
    }

    @Test
    void testAddPlace_WhenNull_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> placeService.addPlace(null));
    }

    @Test
    void testFindPlaceByName_WhenFound_ShouldReturnPlace() {
        String name = "Colosseo";
        Places expected = new Places();
        expected.setText(name);

        when(placesRepository.findByText(name)).thenReturn(expected);

        Places result = placeService.findPlaceByName(name);

        assertEquals(expected, result);
    }

    @Test
    void testFindPlaceByName_WhenNotFound_ShouldThrowException() {
        String name = "NonEsiste";
        when(placesRepository.findByText(name)).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> placeService.findPlaceByName(name));
    }

    @Test
    void testSearchNearby_WhenInDb_ShouldReturnFromDb() {
        // Arrange
        Location loc = new Location(10.0, 10.0);
        String city = "Roma";
        String country = "IT";

        Places placeEntity = new Places();
        placeEntity.setId(1L);
        placeEntity.setText("DB Place");
        
        when(placesRepository.findByCitySummary_CityAndCitySummary_Country(city, country))
            .thenReturn(List.of(placeEntity));

        // Act
        List<PlacesDTO> result = placeService.searchNearby(loc, city, country);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("DB Place", result.get(0).name());
        
        verify(webClientBuilder, never()).build();
    }

    @Test
    void testSearchNearby_WhenNotInDb_ShouldCallApi() {
        // Arrange
        Location loc = new Location(10.0, 10.0);
        String city = "Roma";
        String country = "IT";
        String apiKey = "API_KEY";

        when(placesRepository.findByCitySummary_CityAndCitySummary_Country(city, country))
            .thenReturn(Collections.emptyList());

        when(googleAuthService.getApiKey()).thenReturn(apiKey);

        PlaceResponse mockResponse = createMockPlaceResponse("places/API1", "API Place");
        
        mockWebClientPostChain(mockResponse);

        // Act
        List<PlacesDTO> result = placeService.searchNearby(loc, city, country);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("API Place", result.get(0).name());
        
        verify(placesRepository).save(any(Places.class));
    }

    // --- TEST: searchByText ---

    @Test
    void testSearchByText_WhenQueryEmpty_ShouldReturnEmpty() {
        assertTrue(placeService.searchByText(new Location(0.0,0.0), "City", "CC", "").isEmpty());
        assertTrue(placeService.searchByText(new Location(0.0,0.0), "City", "CC", null).isEmpty());
    }

    @Test
    void testSearchByText_WhenValid_ShouldCallApi() {
        // Arrange
        String query = "Pizza";
        Location loc = new Location(10.0, 10.0);
        String apiKey = "KEY";

        when(googleAuthService.getApiKey()).thenReturn(apiKey);
        
        PlaceResponse mockResponse = createMockPlaceResponse("places/piz1", "Pizzeria");
        mockWebClientPostChain(mockResponse);

        // Act
        List<PlacesDTO> result = placeService.searchByText(loc, "Napoli", "IT", query);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Pizzeria", result.get(0).name());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testMapPlacesToDTOs_WithPhotos_ShouldCallPhotoApi() {

        // Arrange
        String apiKey = "KEY";
        when(googleAuthService.getApiKey()).thenReturn(apiKey);

        // Creiamo una risposta che contiene una foto
        PlaceResponse placeResponse = new PlaceResponse();
        PlacesData data = new PlacesData();
        data.setName("places/photoTest");
        data.setDisplayName(new DisplayName("Photo Place", "en"));
        
        Photo photo = new Photo();
        photo.setName("photos/123");
        data.setPhotos(new Photo[]{ photo });
        
        placeResponse.setPlaces(List.of(data));

        PhotoResponse photoApiResp = new PhotoResponse();
        photoApiResp.setPhotoUri("http://google.com/img.jpg");

        WebClient.RequestHeadersUriSpec requestHeadersUriSpecCheck = mock(WebClient.RequestHeadersUriSpec.class);
        
        when(webClient.get()).thenReturn(requestHeadersUriSpecCheck);
        when(requestHeadersUriSpecCheck.uri(any(java.net.URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PhotoResponse.class)).thenReturn(Mono.just(photoApiResp));

        // Act
        List<PlacesDTO> dtos = placeService.mapPlacesToDTOs(placeResponse, "C", "CC");

        // Assert
        assertFalse(dtos.isEmpty());
        List<String> photoUrls = dtos.get(0).photoUrl();
        assertNotNull(photoUrls);
        assertEquals(1, photoUrls.size());
        assertEquals("http://google.com/img.jpg", photoUrls.get(0));
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientPostChain(PlaceResponse responseToReturn) {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PlaceResponse.class)).thenReturn(Mono.just(responseToReturn));
    }

    private PlaceResponse createMockPlaceResponse(String placeName, String displayName) {
        PlaceResponse response = new PlaceResponse();
        PlacesData data = new PlacesData();
        data.setName(placeName);
        data.setDisplayName(new DisplayName(displayName, "en"));
        data.setFormattedAddress("Address 1");
        data.setPrimaryType("restaurant");
        response.setPlaces(List.of(data));
        return response;
    }
}
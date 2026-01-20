package com.sds2.service.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.coordinates.Location;
import com.sds2.classes.price.PriceRange;
import com.sds2.classes.price.PriceRange.Money;
import com.sds2.dto.AiItineraryPlan;
import com.sds2.dto.AiItineraryPlan.Item;
import com.sds2.dto.ChatActivityDTO;
import com.sds2.dto.POIDTO;
import com.sds2.dto.PlacesDTO;
import com.sds2.service.POIService;
import com.sds2.service.PlaceService;

@ExtendWith(MockitoExtension.class)
class ChatActivityResolverTest {

    @Mock
    private POIService poiService;

    @Mock
    private PlaceService placeService;

    @InjectMocks
    private ChatActivityResolver resolver;

    @Test
    void testResolve_WhenPlanIsEmpty_ShouldReturnEmptyList() {
        // Arrange
        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.items()).thenReturn(Collections.emptyList());
        
        CityContext city = new CityContext("Paris", "FR", 48.85, 2.35);
        ChatPreferences prefs = mock(ChatPreferences.class);

        // Act
        List<ChatActivityDTO> result = resolver.resolve(plan, city, prefs);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testResolve_WhenGoogleMatchFound_ShouldReturnGoogleActivity() {
        // Arrange
        CityContext city = new CityContext("Rome", "IT", 41.90, 12.50);
        ChatPreferences prefs = mock(ChatPreferences.class);

        Item item = mock(Item.class);
        when(item.name()).thenReturn("Colosseum");
        
        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.items()).thenReturn(List.of(item));

        // Mock Google Response
        PlacesDTO googlePlace = createGooglePlace("Colosseum", "Tourist Attraction", 4.8);
        when(placeService.searchByText(any(Location.class), anyString(), anyString(), anyString()))
            .thenReturn(List.of(googlePlace));

        // Act
        List<ChatActivityDTO> result = resolver.resolve(plan, city, prefs);

        // Assert
        assertFalse(result.isEmpty());
        ChatActivityDTO dto = result.get(0);
        assertEquals("google", dto.source());
        assertEquals("Colosseum", dto.name());
        assertEquals(4.8, dto.rating());
    }

@Test
    void testResolve_WhenAmadeusMatchFound_ShouldReturnAmadeusActivity() {
        // Arrange
        CityContext city = new CityContext("London", "UK", 51.50, -0.12);
        ChatPreferences prefs = new ChatPreferences(Set.of("landmark"), false);

        Item item = mock(Item.class);
        when(item.name()).thenReturn("Big Ben");
        when(item.type()).thenReturn("landmark");
        
        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.items()).thenReturn(List.of(item));

        GeoCode coords = new GeoCode(51.5007, -0.1246);
        POIDTO amadeusPoi = new POIDTO(
            "Big Ben", 
            "Historic Site", 
            "Iconic clock tower", 
            "landmark", 
            null, 
            "1h", 
            "http://booking.com", 
            null, 
            coords
        );

        when(poiService.getPointOfInterests(any(), any(), any()))
            .thenReturn(List.of(amadeusPoi));
        
        when(placeService.searchByText(any(), any(), any(), any()))
            .thenReturn(Collections.emptyList());

        // Act
        List<ChatActivityDTO> result = resolver.resolve(plan, city, prefs);

        // Assert
        assertNotNull(result, "Result list should not be null");
        
        ChatActivityDTO dto = result.get(0);
        assertEquals("amadeus", dto.source());
        assertEquals("Big Ben", dto.name());
    }

    @Test
    void testResolve_WhenDuplicateItems_ShouldDeduplicate() {
        // Arrange
        CityContext city = new CityContext("Paris", "FR", 48.85, 2.35);
        ChatPreferences prefs = mock(ChatPreferences.class);

        Item item1 = mock(Item.class); when(item1.name()).thenReturn("Louvre");
        Item item2 = mock(Item.class); when(item2.name()).thenReturn("Louvre"); // Duplicate name
        
        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.items()).thenReturn(List.of(item1, item2));

        PlacesDTO place = createGooglePlace("Louvre", "Museum", 4.9);
        when(placeService.searchByText(any(), any(), any(), any()))
            .thenReturn(List.of(place));

        // Act
        List<ChatActivityDTO> result = resolver.resolve(plan, city, prefs);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testResolve_WhenScoreIsTooLow_ShouldReturnEmpty() {
        // Arrange
        CityContext city = new CityContext("Nowhere", "XX", 0.0, 0.0);
        ChatPreferences prefs = mock(ChatPreferences.class);

        Item item = mock(Item.class);
        when(item.name()).thenReturn("Very Specific Place");

        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.items()).thenReturn(List.of(item));

        PlacesDTO randomPlace = createGooglePlace("Random Burger Shop", "Food", 3.0);
        when(placeService.searchByText(any(), any(), any(), any()))
            .thenReturn(List.of(randomPlace));

        // Act
        List<ChatActivityDTO> result = resolver.resolve(plan, city, prefs);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testResolve_PreferencesTypeMatch_ShouldBoostScore() {
        // Arrange
        CityContext city = new CityContext("Milan", "IT", 45.46, 9.19);
        ChatPreferences prefs = mock(ChatPreferences.class);
        
        when(prefs.matches(anyString())).thenReturn(true);

        Item item = mock(Item.class);
        when(item.name()).thenReturn("Duomo");
        when(item.type()).thenReturn("Cathedral");

        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.items()).thenReturn(List.of(item));

        PlacesDTO place = createGooglePlace("Duomo di Milano", "Church", 4.8);
        when(placeService.searchByText(any(), any(), any(), any()))
            .thenReturn(List.of(place));

        // Act
        List<ChatActivityDTO> result = resolver.resolve(plan, city, prefs);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("Duomo di Milano", result.get(0).name());
    }


    private PlacesDTO createGooglePlace(String name, String type, Double rating) {
        
        // Constructing PriceRange for Google
        Money startPrice = new Money();
        startPrice.setUnits("10");
        startPrice.setCurrencyCode("EUR");
        PriceRange pr = new PriceRange();
        pr.setStartPrice(startPrice);

        return new PlacesDTO(
            1L, name, List.of(), type, "Address 1", 
            new Location(0.0,0.0), rating, pr, "http://uri"
        );
    }

}
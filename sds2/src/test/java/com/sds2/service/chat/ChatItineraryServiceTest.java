package com.sds2.service.chat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sds2.dto.AiItineraryPlan;
import com.sds2.dto.ChatActivityDTO;
import com.sds2.dto.ChatItineraryRequest;
import com.sds2.dto.ChatItineraryResponse;
import com.sds2.service.ChatItineraryService;

@ExtendWith(MockitoExtension.class)
class ChatItineraryServiceTest {

    @Mock
    private ChatCityResolver cityResolver;
    @Mock
    private ChatPreferenceExtractor preferenceExtractor;
    @Mock
    private AiItineraryClient aiClient;
    @Mock
    private ChatActivityResolver activityResolver;
    @Mock
    private ChatItineraryFormatter itineraryFormatter;
    @Mock
    private ChatFeasibilityEvaluator feasibilityEvaluator;

    @InjectMocks
    private ChatItineraryService service;

    @Test
    void testGenerateItinerary_NullRequest_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.generateItinerary(null));
    }

    @Test
    void testGenerateItinerary_InvalidPrompt_ThrowsException() {
        ChatItineraryRequest reqNull = mockRequest(null, "Paris");
        assertThrows(IllegalArgumentException.class, () -> service.generateItinerary(reqNull));

        ChatItineraryRequest reqEmpty = mockRequest("   ", "Paris");
        assertThrows(IllegalArgumentException.class, () -> service.generateItinerary(reqEmpty));
    }

    @Test
    void testGenerateItinerary_InvalidDestination_ThrowsException() {
        ChatItineraryRequest reqNull = mockRequest("Plan a trip", null);
        assertThrows(IllegalArgumentException.class, () -> service.generateItinerary(reqNull));

        ChatItineraryRequest reqEmpty = mockRequest("Plan a trip", "   ");
        assertThrows(IllegalArgumentException.class, () -> service.generateItinerary(reqEmpty));
    }

    @Test
    void testGenerateItinerary_SuccessFlow_WithValidDates() {
        String startDate = "2025-06-01";
        String endDate = "2025-06-03"; // 3 days
        ChatItineraryRequest request = mockRequest("Trip to Rome", "Rome", startDate, endDate);

        CityContext mockCity = new CityContext("Rome", "IT", 41.9, 12.5);
        ChatPreferences mockPrefs = mock(ChatPreferences.class);
        AiItineraryPlan mockPlan = mock(AiItineraryPlan.class);
        List<ChatActivityDTO> mockActivities = List.of(mock(ChatActivityDTO.class));

        when(cityResolver.resolve(request)).thenReturn(mockCity);
        when(preferenceExtractor.extract(request)).thenReturn(mockPrefs);
        when(aiClient.generatePlan(request, mockCity, 3, mockPrefs)).thenReturn(mockPlan);
        when(itineraryFormatter.format(mockPlan, startDate, 3)).thenReturn("Formatted Itinerary");
        when(feasibilityEvaluator.buildMessage(3, mockPlan)).thenReturn("Looks good");
        when(activityResolver.resolve(mockPlan, mockCity, mockPrefs)).thenReturn(mockActivities);

        ChatItineraryResponse response = service.generateItinerary(request);

        assertNotNull(response);
        assertEquals("Formatted Itinerary", response.itinerary());
        assertEquals("Looks good", response.feasibility());
        assertEquals(3, response.days());
        assertEquals(1, response.activityCount());
        assertEquals(mockActivities, response.activities());

        verify(aiClient).generatePlan(request, mockCity, 3, mockPrefs);
    }

    @Test
    void testGenerateItinerary_DateFallback_WhenDatesMissing() {
        ChatItineraryRequest request = mockRequest("Trip", "Paris", null, null);

        CityContext mockCity = new CityContext("Paris", "FR", 0.0, 0.0);
        when(cityResolver.resolve(request)).thenReturn(mockCity);
        when(aiClient.generatePlan(any(), any(), eq(1), any())).thenReturn(mock(AiItineraryPlan.class));
        when(activityResolver.resolve(any(), any(), any())).thenReturn(Collections.emptyList());
        when(itineraryFormatter.format(any(), any(), eq(1))).thenReturn("");

        ChatItineraryResponse response = service.generateItinerary(request);

        assertEquals(1, response.days());
    }

    @Test
    void testGenerateItinerary_DateFallback_WhenDatesInvalid() {
        ChatItineraryRequest request = mockRequest("Trip", "Paris", "invalid-date", "2025-01-01");

        CityContext mockCity = new CityContext("Paris", "FR", 0.0, 0.0);
        when(cityResolver.resolve(request)).thenReturn(mockCity);
        when(aiClient.generatePlan(any(), any(), eq(1), any())).thenReturn(mock(AiItineraryPlan.class));
        when(activityResolver.resolve(any(), any(), any())).thenReturn(Collections.emptyList());

        ChatItineraryResponse response = service.generateItinerary(request);

        assertEquals(1, response.days());
    }

    private ChatItineraryRequest mockRequest(String prompt, String destination) {
        return mockRequest(prompt, destination, null, null);
    }

    private ChatItineraryRequest mockRequest(String prompt, String destination, String start, String end) {
        ChatItineraryRequest req = mock(ChatItineraryRequest.class);
        lenient().when(req.prompt()).thenReturn(prompt);
        lenient().when(req.destination()).thenReturn(destination);
        lenient().when(req.startDate()).thenReturn(start);
        lenient().when(req.endDate()).thenReturn(end);
        
        lenient().when(req.latitude()).thenReturn(null);
        lenient().when(req.longitude()).thenReturn(null);
        lenient().when(req.countryCode()).thenReturn(null);
        return req;
    }
}
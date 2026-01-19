package com.sds2.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sds2.dto.AiItineraryPlan;
import com.sds2.dto.ChatActivityDTO;
import com.sds2.dto.ChatItineraryRequest;
import com.sds2.dto.ChatItineraryResponse;
import com.sds2.service.chat.AiItineraryClient;
import com.sds2.service.chat.ChatActivityResolver;
import com.sds2.service.chat.ChatCityResolver;
import com.sds2.service.chat.ChatFeasibilityEvaluator;
import com.sds2.service.chat.ChatItineraryFormatter;
import com.sds2.service.chat.ChatPreferenceExtractor;
import com.sds2.service.chat.ChatPreferences;
import com.sds2.service.chat.CityContext;

@Service
public class ChatItineraryService {
    private final ChatCityResolver cityResolver;
    private final ChatPreferenceExtractor preferenceExtractor;
    private final AiItineraryClient aiClient;
    private final ChatActivityResolver activityResolver;
    private final ChatItineraryFormatter itineraryFormatter;
    private final ChatFeasibilityEvaluator feasibilityEvaluator;

    public ChatItineraryService(
        ChatCityResolver cityResolver,
        ChatPreferenceExtractor preferenceExtractor,
        AiItineraryClient aiClient,
        ChatActivityResolver activityResolver,
        ChatItineraryFormatter itineraryFormatter,
        ChatFeasibilityEvaluator feasibilityEvaluator
    ) {
        this.cityResolver = cityResolver;
        this.preferenceExtractor = preferenceExtractor;
        this.aiClient = aiClient;
        this.activityResolver = activityResolver;
        this.itineraryFormatter = itineraryFormatter;
        this.feasibilityEvaluator = feasibilityEvaluator;
    }

    public ChatItineraryResponse generateItinerary(ChatItineraryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request is required.");
        }
        if (request.prompt() == null || request.prompt().trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt is required.");
        }
        if (request.destination() == null || request.destination().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination is required.");
        }

        int days = resolveDays(request.startDate(), request.endDate());
        CityContext city = cityResolver.resolve(request);
        ChatPreferences preferences = preferenceExtractor.extract(request);
        AiItineraryPlan plan = aiClient.generatePlan(request, city, days, preferences);
        String itinerary = itineraryFormatter.format(plan, request.startDate(), days);
        String feasibility = feasibilityEvaluator.buildMessage(days, plan);
        List<ChatActivityDTO> activityCards = activityResolver.resolve(plan, city, preferences);

        return new ChatItineraryResponse(
            itinerary,
            feasibility,
            days,
            activityCards.size(),
            activityCards
        );
    }

    private int resolveDays(String startDate, String endDate) {
        if (startDate == null || endDate == null) return 1;
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            return days > 0 ? (int) days : 1;
        } catch (DateTimeParseException _) {
            return 1;
        }
    }
}

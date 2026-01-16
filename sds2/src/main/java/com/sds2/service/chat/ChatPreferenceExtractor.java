package com.sds2.service.chat;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sds2.dto.ChatItineraryRequest;

@Service
public class ChatPreferenceExtractor {
    private static final String TAG_MUSEUM = "museum";
    private static final String TAG_PARK = "park";
    private static final String TAG_HISTORICAL = "historical";
    private static final String TAG_SHOPPING = "shopping";
    private static final String TAG_NIGHTLIFE = "nightlife";
    private static final String TAG_FOOD = "food";

    private static final Map<String, List<String>> FILTER_KEYWORDS = Map.of(
        TAG_MUSEUM, List.of(TAG_MUSEUM, "gallery", "exhibit", "art"),
        TAG_PARK, List.of(TAG_PARK, "garden", "nature", "botanical"),
        TAG_HISTORICAL, List.of("historic", "history", "heritage", "monument", "castle", "cathedral"),
        TAG_SHOPPING, List.of(TAG_SHOPPING, "market", "mall", "boutique", "store")
    );
    private static final Map<String, List<String>> PROMPT_KEYWORDS = Map.of(
        TAG_MUSEUM, List.of(TAG_MUSEUM, "musee", "gallery", "galerie"),
        TAG_PARK, List.of(TAG_PARK, "parc", "garden", "jardin", "nature"),
        TAG_HISTORICAL, List.of("historic", "historique", "history", "histoire", "monument", "castle", "chateau", "cathedral", "cathedrale", "eglise", "church"),
        TAG_SHOPPING, List.of(TAG_SHOPPING, "market", "marche", "mall", "boutique"),
        TAG_NIGHTLIFE, List.of(TAG_NIGHTLIFE, "bar", "club", "pub"),
        TAG_FOOD, List.of(TAG_FOOD, "cuisine", "restaurant", "gastronomie", "local")
    );
    private static final List<String> FREE_ONLY_KEYWORDS = List.of(
        "free", "gratuit", "gratis", "sans frais", "pas cher", "budget"
    );

    public ChatPreferences extract(ChatItineraryRequest request) {
        Set<String> keywords = new LinkedHashSet<>();
        List<String> filters = request.filters();
        if (filters != null) {
            for (String filter : filters) {
                List<String> mapped = FILTER_KEYWORDS.get(filter);
                if (mapped != null) {
                    keywords.addAll(mapped);
                }
            }
        }

        String prompt = request.prompt();
        if (prompt != null && !prompt.isBlank()) {
            String lower = prompt.toLowerCase(Locale.ROOT);
            for (List<String> values : PROMPT_KEYWORDS.values()) {
                for (String token : values) {
                    if (lower.contains(token)) {
                        keywords.add(token);
                    }
                }
            }
        }

        return new ChatPreferences(keywords, isFreeOnly(prompt));
    }

    private boolean isFreeOnly(String prompt) {
        if (prompt == null) return false;
        String lower = prompt.toLowerCase(Locale.ROOT);
        for (String keyword : FREE_ONLY_KEYWORDS) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}

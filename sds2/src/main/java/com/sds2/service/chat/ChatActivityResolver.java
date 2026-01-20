package com.sds2.service.chat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.coordinates.Location;
import com.sds2.dto.AiItineraryPlan;
import com.sds2.dto.AiItineraryPlan.Item;
import com.sds2.dto.ChatActivityDTO;
import com.sds2.dto.POIDTO;
import com.sds2.dto.PlacesDTO;
import com.sds2.service.POIService;
import com.sds2.service.PlaceService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChatActivityResolver {
    private static final double NAME_MIN_SCORE = 0.35;
    private static final double NAME_WEIGHT = 0.65;
    private static final double TYPE_WEIGHT = 0.15;
    private static final double RATING_WEIGHT = 0.15;
    private static final double FREE_PENALTY = 0.35;
    private static final int AMADEUS_CANDIDATE_LIMIT = 6;

    private final POIService poiService;
    private final PlaceService placeService;
    private final TextNormalizer textNormalizer;

    public List<ChatActivityDTO> resolve(
            AiItineraryPlan plan,
            CityContext city,
            ChatPreferences preferences
    ) {
        if (plan == null || plan.items().isEmpty()) {
            return List.of();
        }

        List<ActivityCandidate> amadeusCandidates = buildAmadeusCandidates(city);
        Map<String, ChatActivityDTO> cache = new HashMap<>();
        Set<String> usedKeys = new HashSet<>();

        return plan.items().stream()
                .filter(Objects::nonNull)
                .filter(item -> item.name() != null && !item.name().isBlank())
                .map(item -> processItem(item, city, preferences, amadeusCandidates, cache, usedKeys))
                .filter(Objects::nonNull)
                .toList();

    }

    private ChatActivityDTO processItem(
        Item item,
        CityContext city,
        ChatPreferences preferences,
        List<ActivityCandidate> amadeusCandidates,
        Map<String, ChatActivityDTO> cache,
        Set<String> usedKeys
    ) {
        String key = textNormalizer.normalize(item.name());
        if (key.isBlank()) return null;

        if (cache.containsKey(key)) {
            ChatActivityDTO cached = cache.get(key);
            return (cached != null && usedKeys.add(key)) ? cached : null;
        }

        List<ActivityCandidate> candidates = new ArrayList<>();
        candidates.addAll(buildGoogleCandidates(item, city));
        candidates.addAll(filterAmadeusCandidates(amadeusCandidates, item.name()));

        ActivityCandidate best = pickBest(candidates, item, preferences);
        ChatActivityDTO dto = best != null ? toChatActivityDTO(best) : null;

        cache.put(key, dto);
        return (dto != null && usedKeys.add(key)) ? dto : null;
    }


    private List<ActivityCandidate> buildAmadeusCandidates(CityContext city) {
        List<POIDTO> activities = poiService.getPointOfInterests(
            new GeoCode(city.latitude(), city.longitude()),
            city.destination(),
            city.country()
        );
        if (activities == null || activities.isEmpty()) {
            return List.of();
        }
        List<ActivityCandidate> candidates = new ArrayList<>();
        for (POIDTO poi : activities) {
            if (poi == null) continue;
            candidates.add(toCandidate(poi));
        }
        return candidates;
    }

    private List<ActivityCandidate> buildGoogleCandidates(Item item, CityContext city) {
        String query = item.name();
        if (city.destination() != null && !city.destination().isBlank()) {
            query = query + " " + city.destination();
        }
        List<PlacesDTO> places = placeService.searchByText(
            new Location(city.latitude(), city.longitude()),
            city.destination(),
            city.country(),
            query
        );
        if (places == null || places.isEmpty()) {
            return List.of();
        }
        List<ActivityCandidate> candidates = new ArrayList<>();
        for (PlacesDTO place : places) {
            if (place == null) continue;
            candidates.add(toCandidate(place));
        }
        return candidates;
    }

    private List<ActivityCandidate> filterAmadeusCandidates(
        List<ActivityCandidate> candidates,
        String itemName
    ) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        return candidates.stream()
            .filter(candidate -> nameSimilarity(itemName, candidate.name()) >= NAME_MIN_SCORE)
            .sorted(Comparator.comparingDouble((ActivityCandidate candidate) ->
                nameSimilarity(itemName, candidate.name())).reversed())
            .limit(AMADEUS_CANDIDATE_LIMIT)
            .toList();
    }

    private ActivityCandidate pickBest(
        List<ActivityCandidate> candidates,
        Item item,
        ChatPreferences preferences
    ) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        ActivityCandidate best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (ActivityCandidate candidate : candidates) {
            double score = scoreCandidate(candidate, item, preferences);
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        return bestScore >= NAME_MIN_SCORE ? best : null;
    }

    private double scoreCandidate(ActivityCandidate candidate, Item item, ChatPreferences preferences) {
        double nameScore = nameSimilarity(item.name(), candidate.name());

        double typeScore = calculateTypeScore(candidate, item, preferences);

        double ratingScore = calculateRatingScore(candidate);

        double score = (nameScore * NAME_WEIGHT)
                    + (typeScore * TYPE_WEIGHT)
                    + (ratingScore * RATING_WEIGHT);

        score = applyFreePenalty(score, candidate, preferences);

        return score;
    }

    private double calculateTypeScore(ActivityCandidate candidate, Item item, ChatPreferences preferences) {
        if (item.type() != null && candidate.type() != null) {
            String itemType = textNormalizer.normalize(item.type());
            String candidateType = textNormalizer.normalize(candidate.type());
            if (!itemType.isBlank() && candidateType.contains(itemType)) {
                return 1.0;
            }
        }

        if (preferences != null && (
                preferences.matches(candidate.type(), textNormalizer) ||
                preferences.matches(candidate.description(), textNormalizer) ||
                preferences.matches(candidate.name(), textNormalizer))) {
            return 0.8;
        }

        return 0.0;
    }

    private double calculateRatingScore(ActivityCandidate candidate) {
        if (candidate.rating() == null) return 0.0;
        return Math.min(candidate.rating() / 5.0, 1.0);
    }

    private double applyFreePenalty(double score, ActivityCandidate candidate, ChatPreferences preferences) {
        if (preferences != null && preferences.freeOnly() &&
            candidate.priceAmount() != null && candidate.priceAmount() > 0.01) {
            return score - FREE_PENALTY;
        }
        return score;
    }


    private double nameSimilarity(String left, String right) {
        String a = textNormalizer.normalize(left);
        String b = textNormalizer.normalize(right);
        if (a.isBlank() || b.isBlank()) return 0.0;
        if (a.equals(b)) return 1.0;
        if (a.contains(b) || b.contains(a)) return 0.9;

        Set<String> aTokens = tokenSet(a);
        Set<String> bTokens = tokenSet(b);
        if (aTokens.isEmpty() || bTokens.isEmpty()) return 0.0;
        int intersection = 0;
        for (String token : aTokens) {
            if (bTokens.contains(token)) {
                intersection += 1;
            }
        }
        int max = Math.max(aTokens.size(), bTokens.size());
        return max == 0 ? 0.0 : (double) intersection / max;
    }

    private Set<String> tokenSet(String value) {
        Set<String> tokens = new HashSet<>();
        for (String token : value.split(" ")) {
            if (token.length() >= 3) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private ActivityCandidate toCandidate(POIDTO poi) {
        Double latitude = null;
        Double longitude = null;
        if (poi.coordinates() != null) {
            latitude = poi.coordinates().getLatitude();
            longitude = poi.coordinates().getLongitude();
        }
        Double amount = poi.price() != null ? poi.price().getAmount() : null;
        String currency = poi.price() != null ? poi.price().getCurrencyCode() : null;

        return new ActivityCandidate(
            "amadeus",
            poi.name(),
            poi.description(),
            poi.type(),
            poi.pictures(),
            poi.minimumDuration(),
            poi.bookingLink(),
            amount,
            currency,
            null,
            null,
            null,
            latitude,
            longitude
        );
    }

    private ActivityCandidate toCandidate(PlacesDTO place) {
        String picture = null;
        if (place.photoUrl() != null && !place.photoUrl().isEmpty()) {
            picture = place.photoUrl().get(0);
        }
        Double latitude = null;
        Double longitude = null;
        if (place.location() != null) {
            latitude = place.location().getLatitude();
            longitude = place.location().getLongitude();
        }
        Double amount = parsePrice(place.priceRange());
        String currency = place.priceRange() != null && place.priceRange().getStartPrice() != null
            ? place.priceRange().getStartPrice().getCurrencyCode()
            : null;

        return new ActivityCandidate(
            "google",
            place.name(),
            null,
            place.type(),
            picture,
            null,
            null,
            amount,
            currency,
            place.address(),
            place.rating(),
            place.websiteUri(),
            latitude,
            longitude
        );
    }

    private Double parsePrice(com.sds2.classes.price.PriceRange priceRange) {
        if (priceRange == null || priceRange.getStartPrice() == null) return null;
        String units = priceRange.getStartPrice().getUnits();
        if (units == null || units.isBlank()) return null;
        try {
            return Double.parseDouble(units);
        } catch (NumberFormatException _) {
            return null;
        }
    }

    private ChatActivityDTO toChatActivityDTO(ActivityCandidate activity) {
        return new ChatActivityDTO(
            activity.source(),
            activity.name(),
            activity.description(),
            activity.type(),
            activity.picture(),
            activity.minimumDuration(),
            activity.bookingLink(),
            activity.priceAmount(),
            activity.priceCurrency(),
            activity.address(),
            activity.rating(),
            activity.websiteUri(),
            activity.latitude(),
            activity.longitude()
        );
    }

    private record ActivityCandidate(
        String source,
        String name,
        String description,
        String type,
        String picture,
        String minimumDuration,
        String bookingLink,
        Double priceAmount,
        String priceCurrency,
        String address,
        Double rating,
        String websiteUri,
        Double latitude,
        Double longitude
    ) {}
}

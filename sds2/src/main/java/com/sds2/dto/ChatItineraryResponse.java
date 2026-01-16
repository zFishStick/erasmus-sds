package com.sds2.dto;

import java.util.List;

public record ChatItineraryResponse(
    String itinerary,
    String feasibility,
    int days,
    int activityCount,
    List<ChatActivityDTO> activities
) {}

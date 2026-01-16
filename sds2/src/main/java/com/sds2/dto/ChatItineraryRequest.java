package com.sds2.dto;

import java.util.List;

public record ChatItineraryRequest(
    String prompt,
    String destination,
    String countryCode,
    Double latitude,
    Double longitude,
    String startDate,
    String endDate,
    List<String> filters
) {}

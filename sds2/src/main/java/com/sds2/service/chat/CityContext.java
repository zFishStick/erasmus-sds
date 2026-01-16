package com.sds2.service.chat;

public record CityContext(
    String destination,
    String country,
    double latitude,
    double longitude
) {}

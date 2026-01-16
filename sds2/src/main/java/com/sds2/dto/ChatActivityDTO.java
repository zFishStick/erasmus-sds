package com.sds2.dto;

public record ChatActivityDTO(
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

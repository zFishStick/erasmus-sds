package com.sds2.dto;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.price.PriceRange;

import java.io.Serializable;
import java.util.List;

public record PlacesDTO(
    Long id,
    String name,
    List<String> photoUrl,
    String type,
    String address,
    Location location,
    Double rating,
    PriceRange priceRange,
    String websiteUri
) implements Serializable {
}
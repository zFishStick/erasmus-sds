package com.sds2.dto;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.price.PriceRange;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public record PlacesDTO(
    String name,
    List<String> photoUrl,
    String type,
    String address,
    Location location,
    Double rating,
    PriceRange priceRange,
    String websiteUri
) implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlacesDTO that = (PlacesDTO) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(photoUrl, that.photoUrl) &&
               Objects.equals(type, that.type) &&
               Objects.equals(address, that.address) &&
               Objects.equals(location, that.location) &&
               Objects.equals(rating, that.rating) &&
               Objects.equals(priceRange, that.priceRange) &&
               Objects.equals(websiteUri, that.websiteUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, photoUrl, type, address, location, rating, priceRange, websiteUri);
    }

    @Override
    public String toString() {
        return "PlacesDTO[name=" + name +
               ", photoUrl=" + photoUrl +
               ", type=" + type +
               ", address=" + address +
               ", location=" + location +
               ", rating=" + rating +
               ", priceRange=" + priceRange +
                ", websiteUri=" + websiteUri +
               "]";
    }
}
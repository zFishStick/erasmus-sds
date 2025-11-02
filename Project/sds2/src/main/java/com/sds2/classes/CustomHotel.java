package com.sds2.classes;

import com.fasterxml.jackson.databind.JsonNode;

public class CustomHotel {
    public final String hotelId;
    public final String name;
    public final String address;
    public final Double latitude;
    public final Double longitude;
    public final String rating;
    // distance fields removed

    public CustomHotel(JsonNode node) {
        this.hotelId = node.path("hotelId").asText("").trim();
        this.name = node.path("name").asText("");
        JsonNode addr = node.path("address");
        String line = addr.path("lines").isArray() && addr.path("lines").size() > 0 ? addr.path("lines").get(0).asText("") : "";
        String city = addr.path("cityName").asText("");
        String countryCode = addr.path("countryCode").asText("");
        StringBuilder sb = new StringBuilder();
        if (line != null && !line.isBlank()) sb.append(line);
        if (city != null && !city.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (countryCode != null && !countryCode.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(countryCode);
        }
        this.address = sb.toString();
        JsonNode geo = node.path("geoCode");
        this.latitude = geo.has("latitude") ? geo.path("latitude").asDouble() : null;
        this.longitude = geo.has("longitude") ? geo.path("longitude").asDouble() : null;
        this.rating = node.path("rating").asText("");

        // distance fields removed
    }
}


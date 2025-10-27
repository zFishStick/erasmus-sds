package com.sds2.classes;

import com.fasterxml.jackson.databind.JsonNode;

public class CustomActivity {
    private String name;
    private String description;
    private String price;
    private String imageUrl;
    private String bookingLink;
    private String minimumDuration;

    public CustomActivity(JsonNode node) {
        this.name = node.path("name").asText();
        this.description = node.path("description").asText();
        this.price = node.path("price").path("amount").asText();
        this.imageUrl = node.path("pictures").isArray() && node.path("pictures").size() > 0
                ? node.path("pictures").get(0).asText()
                : null;
        this.bookingLink = node.path("bookingLink").asText();
        this.minimumDuration = node.path("minimumDuration").asText();
    }

    // Getter for Thymeleaf access
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getBookingLink() { return bookingLink; }
    public String getMinimumDuration() { return minimumDuration; }
}


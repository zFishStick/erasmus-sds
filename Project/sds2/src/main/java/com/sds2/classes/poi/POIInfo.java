package com.sds2.classes.poi;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class POIInfo {
    private String name;
    private String type;
    private String pictures;
    private String minimumDuration;
    private String bookingLink;

    @Column(columnDefinition = "TEXT")
    private String description;

    protected POIInfo() {}

    public POIInfo(
        String name, 
        String type, 
        String description,
        String pictures,
        String minimumDuration,
        String bookingLink
    ) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.pictures = pictures;
        this.minimumDuration = minimumDuration;
        this.bookingLink = bookingLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getMinimumDuration() {
        return minimumDuration;
    }

    public void setMinimumDuration(String minimumDuration) {
        this.minimumDuration = minimumDuration;
    }

    public String getBookingLink() {
        return bookingLink;
    }

    public void setBookingLink(String bookingLink) {
        this.bookingLink = bookingLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

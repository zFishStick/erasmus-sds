package com.sds2.amadeus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationModels {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationItem { public String iataCode; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationContainer { public List<LocationItem> data; }
}

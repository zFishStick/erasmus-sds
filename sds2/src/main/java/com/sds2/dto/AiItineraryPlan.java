package com.sds2.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiItineraryPlan(List<Day> days) {
    public List<Item> items() {
        if (days == null || days.isEmpty()) {
            return List.of();
        }
        List<Item> items = new ArrayList<>();
        for (Day day : days) {
            if (day != null && day.items() != null) {
                items.addAll(day.items());
            }
        }
        return items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Day(List<Item> items) {
        public List<Item> safeItems() {
            return items == null ? Collections.emptyList() : items;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
        String time,
        String name,
        String type,
        String reason,
        Double durationHours
    ) {}
}

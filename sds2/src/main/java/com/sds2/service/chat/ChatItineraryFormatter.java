package com.sds2.service.chat;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sds2.dto.AiItineraryPlan;
import com.sds2.dto.AiItineraryPlan.Day;
import com.sds2.dto.AiItineraryPlan.Item;

@Service
public class ChatItineraryFormatter {
    private static final int DEFAULT_START_HOUR = 9;
    private static final int DEFAULT_SLOT_HOURS = 2;

    public String format(AiItineraryPlan plan, String startDate, int days) {
        List<Day> dayPlans = plan.days() == null ? List.of() : plan.days();
        if (dayPlans.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        LocalDate start = parseDate(startDate);
        int dayCount = Math.min(days, dayPlans.size());

        for (int dayIndex = 0; dayIndex < dayCount; dayIndex++) {
            String title = buildDayTitle(dayIndex, start);
            builder.append(title).append("\n");

            List<Item> items = dayPlans.get(dayIndex).safeItems();
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                Item item = items.get(itemIndex);
                if (item == null || item.name() == null || item.name().isBlank()) {
                    continue;
                }
                String time = normalizeTime(item.time(), itemIndex);
                builder.append("- ").append(time).append(": ").append(item.name().trim()).append("\n");
                if (item.reason() != null && !item.reason().isBlank()) {
                    builder.append("  Reason: ").append(item.reason().trim()).append("\n");
                }
            }

            if (dayIndex < dayCount - 1) {
                builder.append("\n");
            }
        }

        return builder.toString().trim();
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException _) {
            return null;
        }
    }

    private String buildDayTitle(int dayIndex, LocalDate start) {
        int dayNumber = dayIndex + 1;
        if (start == null) {
            return "Day " + dayNumber + ": Itinerary";
        }
        LocalDate date = start.plusDays(dayIndex);
        return "Day " + dayNumber + ": " + date;
    }

    private String normalizeTime(String time, int index) {
        if (time != null && time.matches("^\\d{1,2}:\\d{2}$")) {
            String[] parts = time.split(":");
            return String.format("%02d:%s", Integer.parseInt(parts[0]), parts[1]);
        }
        int hour = DEFAULT_START_HOUR + (index * DEFAULT_SLOT_HOURS);
        if (hour > 22) {
            hour = 22;
        }
        return String.format("%02d:00", hour);
    }
}

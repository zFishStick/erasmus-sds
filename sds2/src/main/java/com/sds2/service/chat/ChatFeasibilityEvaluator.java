package com.sds2.service.chat;

import java.util.Locale;

import org.springframework.stereotype.Service;

import com.sds2.dto.AiItineraryPlan;
import com.sds2.dto.AiItineraryPlan.Item;

@Service
public class ChatFeasibilityEvaluator {
    private static final double DEFAULT_ACTIVITY_HOURS = 2.0;
    private static final double HOURS_PER_DAY = 8.0;

    public String buildMessage(int days, AiItineraryPlan plan) {
        double totalHours = 0.0;
        int activityCount = 0;
        if (plan != null) {
            for (Item item : plan.items()) {
                if (item == null) continue;
                activityCount += 1;
                totalHours += item.durationHours() == null ? DEFAULT_ACTIVITY_HOURS : item.durationHours();
            }
        }

        double availableHours = days * HOURS_PER_DAY;
        String level;
        if (totalHours <= availableHours) {
            level = "Looks doable";
        } else if (totalHours <= availableHours * 1.2) {
            level = "Tight but possible";
        } else {
            level = "Likely too dense";
        }
        return String.format(Locale.US,
            "Route check: %s (%d days, %d activities, about %.1f hours total).",
            level, days, activityCount, totalHours
        );
    }
}

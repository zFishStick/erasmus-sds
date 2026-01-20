package com.sds2.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AiItineraryPlanTest {

    @Test
    void items_whenDaysNullOrEmpty_returnsEmptyList() {
        AiItineraryPlan planNull = new AiItineraryPlan(null);
        assertTrue(planNull.items().isEmpty());

        AiItineraryPlan planEmpty = new AiItineraryPlan(List.of());
        assertTrue(planEmpty.items().isEmpty());
    }

    @Test
    void items_aggregatesItemsAndSkipsNullDaysOrNullDayItems() {
        AiItineraryPlan.Item i1 = new AiItineraryPlan.Item("08:00", "Breakfast", "food", null, 0.5);
        AiItineraryPlan.Item i2 = new AiItineraryPlan.Item("10:00", "Museum", "activity", "visit", 2.0);

        AiItineraryPlan.Day dayWithItems = new AiItineraryPlan.Day(List.of(i1));
        AiItineraryPlan.Day dayWithItems2 = new AiItineraryPlan.Day(List.of(i2));
        AiItineraryPlan.Day dayWithNullItems = new AiItineraryPlan.Day(null);

        AiItineraryPlan plan = new AiItineraryPlan(new ArrayList<>(List.of(dayWithItems, dayWithNullItems, dayWithNullItems, dayWithItems2)));

        List<AiItineraryPlan.Item> items = plan.items();
        assertEquals(2, items.size());
        assertTrue(items.contains(i1));
        assertTrue(items.contains(i2));
    }

    @Test
    void day_safeItems_returnsEmptyWhenNull_and_itemsResultIsIndependentCopy() {
        AiItineraryPlan.Day dayNull = new AiItineraryPlan.Day(null);
        assertTrue(dayNull.safeItems().isEmpty());

        AiItineraryPlan.Item i = new AiItineraryPlan.Item("09:00", "Park", "activity", null, 1.0);
        List<AiItineraryPlan.Item> mutable = new ArrayList<>(List.of(i));
        AiItineraryPlan.Day day = new AiItineraryPlan.Day(mutable);
        AiItineraryPlan plan = new AiItineraryPlan(List.of(day));

        List<AiItineraryPlan.Item> aggregated = plan.items();
        // modify aggregated list should not affect original day's list
        aggregated.add(new AiItineraryPlan.Item(null, null, null, null, null));
        assertEquals(1, day.items().size());
        assertEquals(2, aggregated.size());
    }

    @Test
    void items_includesItemsWithNullFields() {
        AiItineraryPlan.Item nullFields = new AiItineraryPlan.Item(null, null, null, null, null);
        AiItineraryPlan.Day day = new AiItineraryPlan.Day(List.of(nullFields));
        AiItineraryPlan plan = new AiItineraryPlan(List.of(day));

        List<AiItineraryPlan.Item> items = plan.items();
        assertEquals(1, items.size());
        assertSame(nullFields, items.get(0));
    }
}
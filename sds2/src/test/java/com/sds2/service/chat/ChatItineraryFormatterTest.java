package com.sds2.service.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sds2.dto.AiItineraryPlan;
import com.sds2.dto.AiItineraryPlan.Day;
import com.sds2.dto.AiItineraryPlan.Item;

class ChatItineraryFormatterTest {

    private ChatItineraryFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new ChatItineraryFormatter();
    }

    @Test
    void testFormat_WhenPlanIsNull_ShouldReturnEmptyString() {
        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.days()).thenReturn(null);

        String result = formatter.format(plan, "2025-01-01", 3);
        assertEquals("", result);
    }

    @Test
    void testFormat_WhenDaysAreEmpty_ShouldReturnEmptyString() {
        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.days()).thenReturn(Collections.emptyList());

        String result = formatter.format(plan, "2025-01-01", 3);
        assertEquals("", result);
    }

@Test
    void testFormat_WithValidData_ShouldFormatCorrectly() {
        // Arrange
        String startDate = "2025-05-10";
        int requestedDays = 1;

        Item item1 = mock(Item.class);
        when(item1.name()).thenReturn("Museo");
        when(item1.time()).thenReturn("10:30");
        when(item1.reason()).thenReturn("Molto bello");

        Item item2 = mock(Item.class);
        when(item2.name()).thenReturn("Pranzo");
        when(item2.time()).thenReturn(null);
        when(item2.reason()).thenReturn(null);

        Day day = mock(Day.class);
        when(day.safeItems()).thenReturn(List.of(item1, item2));

        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.days()).thenReturn(List.of(day));

        // Act
        String result = formatter.format(plan, startDate, requestedDays);

        // Assert
        String expected = """
            Day 1: 2025-05-10
            - 10:30: Museo
              Reason: Molto bello
            - 11:00: Pranzo"""; 

        assertEquals(
            expected.replace("\r", "").trim(), 
            result.replace("\r", "").trim()
        );
    }

    @Test
    void testFormat_WhenDateIsInvalidOrNull_ShouldShowGenericDayTitle() {
        // Arrange
        Item item = mock(Item.class);
        when(item.name()).thenReturn("Attività");
        when(item.time()).thenReturn("09:00");
        
        Day day = mock(Day.class);
        when(day.safeItems()).thenReturn(List.of(item));

        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.days()).thenReturn(List.of(day));

        String resultNullDate = formatter.format(plan, null, 1);
        assertTrue(resultNullDate.contains("Day 1: Itinerary"));

        String resultInvalidDate = formatter.format(plan, "DATA-SBAGLIATA", 1);
        assertTrue(resultInvalidDate.contains("Day 1: Itinerary"));
    }

    @Test
    void testFormat_AutoCalculateTimes() {
        // Arrange
        Item i1 = mock(Item.class); when(i1.name()).thenReturn("A");
        Item i2 = mock(Item.class); when(i2.name()).thenReturn("B");
        Item i3 = mock(Item.class); when(i3.name()).thenReturn("C");

        Day day = mock(Day.class);
        when(day.safeItems()).thenReturn(List.of(i1, i2, i3));

        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.days()).thenReturn(List.of(day));

        // Act
        String result = formatter.format(plan, null, 1);

        // Assert
        assertTrue(result.contains("- 09:00: A"));
        assertTrue(result.contains("- 11:00: B"));
        assertTrue(result.contains("- 13:00: C"));
    }
    
    @Test
    void testFormat_NormalizeExistingTime_ShouldPadZero() {
        Item item = mock(Item.class);
        when(item.name()).thenReturn("Test");
        when(item.time()).thenReturn("9:30");
        
        Day day = mock(Day.class);
        when(day.safeItems()).thenReturn(List.of(item));
        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.days()).thenReturn(List.of(day));
        
        String result = formatter.format(plan, null, 1);
        
        assertTrue(result.contains("09:30"));
    }


    @Test
    void testFormat_ShouldRespectDayLimit() {
        Day d1 = mock(Day.class); when(d1.safeItems()).thenReturn(List.of());
        Day d2 = mock(Day.class); when(d2.safeItems()).thenReturn(List.of());
        Day d3 = mock(Day.class); when(d3.safeItems()).thenReturn(List.of());

        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.days()).thenReturn(List.of(d1, d2, d3));

        // Act
        String result = formatter.format(plan, "2025-01-01", 1);

        // Assert
        assertTrue(result.contains("Day 1"));
        assertTrue(!result.contains("Day 2"));
    }

    @Test
    void testFormat_ShouldSkipInvalidItems() {
        // Arrange
        Item valid = mock(Item.class);
        when(valid.name()).thenReturn("Valid");
        
        Item nullName = mock(Item.class);
        when(nullName.name()).thenReturn(null);
        
        Item blankName = mock(Item.class);
        when(blankName.name()).thenReturn("   ");
        
        Item nullItem = null; // Da saltare

        List<Item> items = java.util.Arrays.asList(valid, nullName, blankName, nullItem);

        Day day = mock(Day.class);
        when(day.safeItems()).thenReturn(items);

        AiItineraryPlan plan = mock(AiItineraryPlan.class);
        when(plan.days()).thenReturn(List.of(day));

        // Act
        String result = formatter.format(plan, null, 1);

        // Assert
        assertTrue(result.contains("Valid"));
        long itemCount = result.lines().filter(line -> line.trim().startsWith("-")).count();
        assertEquals(1, itemCount);
    }
}
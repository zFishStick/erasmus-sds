
package com.sds2.service.chat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper; 

class AiItineraryClientTest {

    @Test
    void extractJsonStripsCodeFences() throws Exception {
        AiItineraryClient client = new AiItineraryClient(WebClient.builder(), new ObjectMapper());
        Method m = AiItineraryClient.class.getDeclaredMethod("extractJson", String.class);
        m.setAccessible(true);
        String content = "Intro text ```json\n{\"items\":[{\"time\":\"09:00\",\"name\":\"Place\",\"type\":\"category\",\"reason\":\"r\",\"durationHours\":2.0}]}\n``` trailing";
        String json = (String) m.invoke(client, content);
        assertNotNull(json);
        assertTrue(json.trim().startsWith("{"));
        assertTrue(json.contains("\"items\""));
    }

    @Test
    void parsePlanParsesAndValidatesNonEmpty() throws Exception {
        AiItineraryClient client = new AiItineraryClient(WebClient.builder(), new ObjectMapper());
        Method parse = AiItineraryClient.class.getDeclaredMethod("parsePlan", String.class);
        parse.setAccessible(true);
        String inner = """
        {
        "days": [
            {
            "items": [
                {
                "time": "09:00",
                "name": "Place",
                "type": "category",
                "reason": "r",
                "durationHours": 2.0
                }
            ]
            }
        ]
        }
        """;
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> responseMap = Map.of(
            "choices", java.util.List.of(
                Map.of(
                    "message", java.util.Map.of("content", "```json\n" + inner + "\n```"),
                    "text", "```json\n" + inner + "\n```"
                )
            )
        );
        String response = mapper.writeValueAsString(responseMap);
        Object plan = parse.invoke(client, response);
        assertNotNull(plan);
        Method items = plan.getClass().getMethod("items");
        Object list = items.invoke(plan);
        assertTrue(list instanceof java.util.Collection);
        assertTrue(((java.util.Collection<?>) list).size() > 0);
    }

    @Test
    void extractContentEmptyResponseThrows() throws Exception {
        AiItineraryClient client = new AiItineraryClient(WebClient.builder(), new ObjectMapper());
        Method m = AiItineraryClient.class.getDeclaredMethod("extractContent", String.class);
        m.setAccessible(true);
        try {
            m.invoke(client, "");
            fail("Expected an exception for empty response");
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof IllegalStateException);
            assertTrue(cause.getMessage().contains("OpenAI response is empty"));
        }
    }

    @Test
    void formatDateRangeHandlesFlexibleAndRange() throws Exception {
        AiItineraryClient client = new AiItineraryClient(WebClient.builder(), new ObjectMapper());
        Method m = AiItineraryClient.class.getDeclaredMethod("formatDateRange", String.class, String.class, int.class);
        m.setAccessible(true);
        String flexible = (String) m.invoke(client, (Object) null, (Object) null, 3);
        assertNotNull(flexible);
        assertTrue(flexible.contains("Flexible"));
        String range = (String) m.invoke(client, "2023-01-01", "2023-01-03", 3);
        assertNotNull(range);
        assertTrue(range.contains("2023-01-01 to 2023-01-03"));
    }

}
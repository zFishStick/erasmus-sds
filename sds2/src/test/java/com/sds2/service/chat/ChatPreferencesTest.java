package com.sds2.service.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

// public record ChatPreferences(Set<String> keywords, boolean freeOnly) {
//     public boolean matches(String value) {
//         if (value == null || value.isBlank() || keywords == null || keywords.isEmpty()) {
//             return false;
//         }
//         String lower = TextNormalizer.normalize(value);
//         for (String keyword : keywords) {
//             if (lower.contains(keyword)) {
//                 return true;
//             }
//         }
//         return false;
//     }
// }

class ChatPreferencesTest {

    @Test
    void testMatches() {
        ChatPreferences prefs = new ChatPreferences(Set.of("beach", "museum"), false);

        
        assertEquals(true, prefs.matches("I love going to the beach!"));

        assertEquals(false, prefs.matches("I enjoy hiking in the mountains."));

        assertEquals(false, prefs.matches(null));
    
        assertEquals(false, prefs.matches(""));
    
        assertEquals(false, prefs.matches("   "));
    }
    
}

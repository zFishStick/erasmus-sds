package com.sds2.service.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

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

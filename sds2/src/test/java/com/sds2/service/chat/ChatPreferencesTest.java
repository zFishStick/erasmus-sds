package com.sds2.service.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

class ChatPreferencesTest {

    private final TextNormalizer textNormalizer = new TextNormalizer();


    @Test
    void testMatches() {

        ChatPreferences prefs = new ChatPreferences(Set.of("beach", "museum"), false);

        
        assertEquals(true, prefs.matches("I love going to the beach!", textNormalizer));

        assertEquals(false, prefs.matches("I enjoy hiking in the mountains.", textNormalizer));

        assertEquals(false, prefs.matches(null, textNormalizer));
    
        assertEquals(false, prefs.matches("", textNormalizer));
    
        assertEquals(false, prefs.matches("   ", textNormalizer));
    }
    
}

package com.sds2.service.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TextNormalizerTest {

    private final TextNormalizer textNormalizer = new TextNormalizer();

    @Test
    void testNormalize() {
        // Test null input
        assertEquals("", textNormalizer.normalize(null));

        // Test empty string
        assertEquals("", textNormalizer.normalize(""));

        // Test string with accents and special characters
        String input1 = "Café Münchën! 123";
        String expected1 = "cafe munchen 123";
        assertEquals(expected1, textNormalizer.normalize(input1));

        // Test string with multiple spaces and punctuation
        String input2 = " Hello,   World!!! ";
        String expected2 = "hello world";
        assertEquals(expected2, textNormalizer.normalize(input2));

        // Test string with mixed case
        String input3 = "TeStInG CaSeS";
        String expected3 = "testing cases";
        assertEquals(expected3, textNormalizer.normalize(input3));
    }
}

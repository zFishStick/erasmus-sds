package com.sds2.classes;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AddressTest {

    @Test
    void defaultConstructorInitializesEmptyLines() {
        Address addr = new Address();
        assertNotNull(addr.getLines());
        assertTrue(addr.getLines().isEmpty());
        assertNull(addr.getCountryCode());
    }

    @Test
    void constructorCopiesProvidedLines() {
        List<String> lines = new ArrayList<>();
        lines.add("123 Main St");
        Address addr = new Address("FR", lines);

        assertEquals("FR", addr.getCountryCode());
        assertEquals(List.of("123 Main St"), addr.getLines());

        // mutate original and ensure defensive copy
        lines.add("SHOULD NOT APPEAR");
        assertEquals(1, addr.getLines().size());
    }

    @Test
    void setLinesDefensiveCopyAndNullHandled() {
        Address addr = new Address();

        addr.setLines(null);
        assertNotNull(addr.getLines());
        assertTrue(addr.getLines().isEmpty());

        List<String> lines = new ArrayList<>();
        lines.add("Line 1");
        addr.setLines(lines);
        assertEquals(List.of("Line 1"), addr.getLines());

        lines.add("Line 2");
        assertEquals(List.of("Line 1"), addr.getLines());

        addr.setCountryCode("ES");
        assertEquals("ES", addr.getCountryCode());
    }
}


package com.sds2.enums;

import org.junit.jupiter.api.Test;

import com.sds2.classes.enums.GoogleBodyEnum;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class GoogleBodyEnumTest {

    @Test
    void getValueMatchesExpected() {
        Map<GoogleBodyEnum, String> expected = Map.of(
            GoogleBodyEnum.CONTENTTYPE, "Content-Type",
            GoogleBodyEnum.APPLICATIONJSON, "application/json",
            GoogleBodyEnum.X_GOOG_API_KEY, "X-Goog-Api-Key",
            GoogleBodyEnum.X_GOOG_FIELD_MASK, "X-Goog-FieldMask"
        );

        expected.forEach((key, val) -> assertEquals(val, key.getValue(), key.name() + " value mismatch"));
    }

    @Test
    void allConstantsPresentAndCountCorrect() {
        String[] expectedNames = {"CONTENTTYPE", "APPLICATIONJSON", "X_GOOG_API_KEY", "X_GOOG_FIELD_MASK"};
        String[] actualNames = Arrays.stream(GoogleBodyEnum.values()).map(Enum::name).toArray(String[]::new);
        assertArrayEquals(expectedNames, actualNames);
        assertEquals(4, GoogleBodyEnum.values().length);
    }

    @Test
    void valueOf_validAnd_invalidCases() {
        assertEquals(GoogleBodyEnum.CONTENTTYPE, GoogleBodyEnum.valueOf("CONTENTTYPE"));
        assertThrows(IllegalArgumentException.class, () -> GoogleBodyEnum.valueOf("NON_EXISTENT"));
        assertThrows(NullPointerException.class, () -> GoogleBodyEnum.valueOf(null));
    }

    @Test
    void valuesAreNonNullNonEmptyAndUnique() {
        GoogleBodyEnum[] values = GoogleBodyEnum.values();
        Set<String> valueSet = new HashSet<>();
        for (GoogleBodyEnum e : values) {
            String v = e.getValue();
            assertNotNull(v, e.name() + " has null value");
            assertFalse(v.isEmpty(), e.name() + " has empty value");
            valueSet.add(v);
        }
        assertEquals(values.length, valueSet.size(), "Duplicate getValue() entries found");
    }

    @Test
    void toStringReturnsName() {
        for (GoogleBodyEnum e : GoogleBodyEnum.values()) {
            assertEquals(e.name(), e.toString());
        }
    }
}
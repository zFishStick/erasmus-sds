package com.sds2.classes.price;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PriceRangeConverter implements AttributeConverter<PriceRange, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(PriceRange attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("Error converting PriceRange to JSON", e);
        }
    }

    @Override
    public PriceRange convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        try {
            return objectMapper.readValue(dbData, PriceRange.class);
        } catch (Exception e) {
            throw new IllegalStateException("Error converting JSON to PriceRange", e);
        }
    }
}



package com.sds2.service.chat;

import java.util.Set;


public record ChatPreferences(Set<String> keywords, boolean freeOnly) {

    public boolean matches(String value, TextNormalizer textNormalizer) {
        if (value == null || value.isBlank() || keywords == null || keywords.isEmpty()) {
            return false;
        }
        
        String lower = textNormalizer.normalize(value);
        for (String keyword : keywords) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}

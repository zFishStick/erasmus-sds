package com.sds2.service.chat;

import java.text.Normalizer;
import java.util.Locale;

final class TextNormalizer {
    private TextNormalizer() {}

    static String normalize(String value) {
        if (value == null) return "";
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.toLowerCase(Locale.ROOT);
        normalized = normalized.replaceAll("[^a-z0-9]+", " ").trim();
        normalized = normalized.replaceAll("\\s+", " ");
        return normalized;
    }
}

package com.sds2.dto;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class OffersPreviewTest {
    @Test
    public void testOffersPreview(){
        Map<String, String> priceMap = Map.of("hello", "goodbye");
        Set<String> checkedIds = Set.of("hello");
        OffersPreview offersPreview = new OffersPreview(priceMap, checkedIds);
    }
}

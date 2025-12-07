package com.sds2.dto;

import java.util.Map;
import java.util.Set;

public record OffersPreview(
        Map<String, String> priceMap,
        Set<String> checkedIds
) {}


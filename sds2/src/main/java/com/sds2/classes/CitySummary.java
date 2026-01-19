package com.sds2.classes;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitySummary {
    private String city;
    private String country;
}

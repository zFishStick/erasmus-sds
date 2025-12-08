package com.sds2.classes.coordinates;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Location {
    private Double latitude;
    private Double longitude;
}

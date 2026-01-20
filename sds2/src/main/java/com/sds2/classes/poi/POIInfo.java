package com.sds2.classes.poi;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class POIInfo {
    private String name;
    private String type;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String pictures;
    private String minimumDuration;
    private String bookingLink;
}

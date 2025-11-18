package com.sds2.classes;

import com.sds2.classes.price.PriceRange;
import com.sds2.classes.price.PriceRangeConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "place")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Places {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private CitySummary citySummary;
    private String name;
    private String text;
    @Column(columnDefinition = "TEXT")
    private String photoUrl;
    private String type;
    private String address;
    @Embedded
    private Location location;
    private Double rating;
    @Column(columnDefinition = "TEXT")
    @Convert(converter = PriceRangeConverter.class)
    private PriceRange priceRange;
    private String websiteUri;
}

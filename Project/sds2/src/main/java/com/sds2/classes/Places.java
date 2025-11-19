package com.sds2.classes;

import java.util.List;

import com.sds2.classes.price.PriceRange;
import com.sds2.classes.price.PriceRangeConverter;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "place_photos", joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "photo_url", columnDefinition = "TEXT")
    private List<String> photoUrl;
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

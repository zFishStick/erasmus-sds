package com.sds2.classes.entity;

import java.util.List;

import com.sds2.classes.enums.RouteTravelMode;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Route {

    @Id
    @GeneratedValue
    private Long id;

    private String routeIdentifier;

    private String city;
    private String country;

    @Enumerated(EnumType.ORDINAL)
    private RouteTravelMode travelMode;

    @ManyToOne
    private Waypoint origin;

    @ManyToOne
    private Waypoint destination;

    @ManyToMany
    @JoinTable(
        name = "route_intermediates",
        joinColumns = @JoinColumn(name = "route_id"),
        inverseJoinColumns = @JoinColumn(name = "waypoint_id")
    )
    private List<Waypoint> intermediates;

    @ManyToOne
    private User user;
}


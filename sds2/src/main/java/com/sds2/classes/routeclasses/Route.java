package com.sds2.classes.routeclasses;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Waypoint origin;

    @ManyToOne
    private Waypoint destination;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Waypoint> intermediates = new ArrayList<>();

    private RouteTravelMode travelMode;
    private int distanceMeters;
    private String duration;
    private String departureTime;
    private String arrivalTime;
}

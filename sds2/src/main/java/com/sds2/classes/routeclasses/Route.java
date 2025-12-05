package com.sds2.classes.routeclasses;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Entity
@Builder
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String routeIdentifier;

    @ManyToOne
    private Waypoint origin;

    @ManyToOne
    private Waypoint destination;

    @OneToMany(cascade = CascadeType.ALL)
    @Builder.Default
    @JoinColumn(name = "route_id")
    private List<Waypoint> intermediates = new ArrayList<>();

    private RouteTravelMode travelMode;
}

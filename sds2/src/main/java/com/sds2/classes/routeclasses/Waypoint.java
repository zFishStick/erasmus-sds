package com.sds2.classes.routeclasses;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.request.WaypointRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Builder
public class Waypoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean via;
    private String name;
    private Location location;
    private String address;
    private String destination;
    private String country;

    @JoinColumn(name = "place_id", unique = true)
    private Long placeId;

    public Waypoint(WaypointRequest waypointRequest, Long placeId) {
        this.via = false;
        this.name = waypointRequest.getName();
        this.location = new Location(waypointRequest.getLat(), waypointRequest.getLng());
        this.address = waypointRequest.getAddress();
        this.placeId = placeId;
        this.destination = waypointRequest.getDestination();
        this.country = waypointRequest.getCountry();
    }
}

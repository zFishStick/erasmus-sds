package com.sds2.classes.entity;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.request.WaypointRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Long userId;

    public Waypoint(WaypointRequest waypointRequest) {
        this.via = false;
        this.name = waypointRequest.getName();
        this.location = new Location(waypointRequest.getLatitude(), waypointRequest.getLongitude());
        this.address = waypointRequest.getAddress();
        this.destination = waypointRequest.getDestination();
        this.country = waypointRequest.getCountry();
        this.userId = waypointRequest.getUserId();
    }
}

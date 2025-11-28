package com.sds2.classes.routeclasses;

import com.sds2.classes.Location;
import com.sds2.classes.Places;
import com.sds2.classes.request.WaypointRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Waypoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean via;
    private String name;
    private Location location;
    private String address;

    @ManyToOne
    @JoinColumn(name = "place_id", unique = true)
    private Places place;

    public Waypoint(WaypointRequest waypointRequest, Places place) {
        this.via = false;
        this.name = waypointRequest.getName();
        this.location = new Location(waypointRequest.getLatitude(), waypointRequest.getLongitude());
        this.address = waypointRequest.getAddress();
        this.place = place;
    }
}

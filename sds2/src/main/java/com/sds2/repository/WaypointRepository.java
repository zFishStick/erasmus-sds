package com.sds2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.Places;
import com.sds2.classes.routeclasses.Waypoint;

@Repository
public interface WaypointRepository extends JpaRepository<Waypoint, Long> {
    Waypoint findByPlace(Places place);
}

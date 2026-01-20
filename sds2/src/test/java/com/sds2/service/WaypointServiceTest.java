package com.sds2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.entity.User;
import com.sds2.classes.entity.Waypoint;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.dto.WaypointDTO;
import com.sds2.repository.WaypointRepository;

@ExtendWith(MockitoExtension.class)
class WaypointServiceTest {

    @Mock
    private WaypointRepository waypointRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private WaypointService waypointService;

    @Test
    void testAddWaypointForUser_WhenNewWaypoint_ShouldCreateSaveAndLinkToUser() {
        // Arrange
        Long userId = 1L;
        WaypointRequest req = new WaypointRequest();
        req.setLatitude(10.0);
        req.setLongitude(20.0);
        req.setName("New place");
        req.setDestination("Roma");
        req.setCountry("IT");

        User user = mock(User.class);
        Set<Waypoint> userWaypoints = new HashSet<>();
        when(userService.findById(userId)).thenReturn(user);
        when(user.getSavedWaypoints()).thenReturn(userWaypoints);

        when(waypointRepository.findByLocation_LatitudeAndLocation_Longitude(10.0, 20.0))
            .thenReturn(null);

        // Act
        String result = waypointService.addWaypointForUser(req, userId);

        // Assert
        assertEquals("Waypoint added successfully", result);
        
        verify(waypointRepository).save(any(Waypoint.class));
        
        verify(userService).saveUser(user);
        
        assertFalse(userWaypoints.isEmpty());
        assertEquals("New place", userWaypoints.iterator().next().getName());
    }

    @Test
    void testAddWaypointForUser_WhenExistingWaypointNotInUser_ShouldLinkExisting() {
        // Arrange
        Long userId = 1L;
        WaypointRequest req = new WaypointRequest();
        req.setLatitude(10.0);
        req.setLongitude(20.0);

        User user = mock(User.class);
        Set<Waypoint> userWaypoints = new HashSet<>();
        when(userService.findById(userId)).thenReturn(user);
        when(user.getSavedWaypoints()).thenReturn(userWaypoints);

        Waypoint existingWaypoint = new Waypoint();
        existingWaypoint.setId(99L);
        existingWaypoint.setName("Esistente");
        
        when(waypointRepository.findByLocation_LatitudeAndLocation_Longitude(10.0, 20.0))
            .thenReturn(existingWaypoint);

        // Act
        String result = waypointService.addWaypointForUser(req, userId);

        // Assert
        assertEquals("Waypoint added successfully", result);
        
        verify(waypointRepository, never()).save(any(Waypoint.class));
        
        verify(userService).saveUser(user);

        assertEquals(1, userWaypoints.size());
        assertEquals(99L, userWaypoints.iterator().next().getId());
    }

    @Test
    void testAddWaypointForUser_WhenAlreadyLinked_ShouldReturnMessage() {
        // Arrange
        Long userId = 1L;
        WaypointRequest req = new WaypointRequest();
        req.setLatitude(10.0);
        req.setLongitude(20.0);

        // Mock Repository: Il waypoint ESISTE
        Waypoint existingWaypoint = new Waypoint();
        existingWaypoint.setId(99L);
        
        User user = mock(User.class);
        Set<Waypoint> userWaypoints = new HashSet<>();
        userWaypoints.add(existingWaypoint);
        
        when(userService.findById(userId)).thenReturn(user);
        when(user.getSavedWaypoints()).thenReturn(userWaypoints);
        
        when(waypointRepository.findByLocation_LatitudeAndLocation_Longitude(10.0, 20.0))
            .thenReturn(existingWaypoint);

        // Act
        String result = waypointService.addWaypointForUser(req, userId);

        // Assert
        assertEquals("You have already added this waypoint", result);
        verify(userService, never()).saveUser(any());
    }

    @Test
    void testRemoveWaypoint_ShouldCallDelete() {
        String result = waypointService.removeWaypoint(123L);
        
        verify(waypointRepository).deleteById(123L);
        assertEquals("Waypoint removed successfully", result);
    }

    @Test
    void testGetWaypointsByDestinationAndCountry_ShouldMapToDTO() {
        // Arrange
        String dest = "Paris";
        String country = "FR";
        
        Waypoint w1 = new Waypoint();
        w1.setId(1L);
        w1.setName("Torre Eiffel");
        w1.setDestination(dest);
        w1.setCountry(country);
        w1.setLocation(new Location(1.0, 2.0));

        when(waypointRepository.findByDestinationAndCountry(dest, country))
            .thenReturn(List.of(w1));

        // Act
        List<WaypointDTO> dtos = waypointService.getWaypointsByDestinationAndCountry(dest, country);

        // Assert
        assertEquals(1, dtos.size());
        assertEquals("Torre Eiffel", dtos.get(0).name()); 
        assertEquals(dest, dtos.get(0).destination());
    }

    @Test
    void testFindByUserAndCity_ShouldFilterCorrectly() {
        // Arrange
        Long userId = 1L;
        String city = "London";
        String country = "UK";


        Waypoint wMatch = createWaypoint(1L, "London", "UK");
        Waypoint wWrongCity = createWaypoint(2L, "Liverpool", "UK");
        Waypoint wWrongCountry = createWaypoint(3L, "London", "US"); // Es. London, Ohio

        User user = mock(User.class);
        when(userService.findById(userId)).thenReturn(user);
        when(user.getSavedWaypoints()).thenReturn(new HashSet<>(List.of(wMatch, wWrongCity, wWrongCountry)));

        // Act
        List<WaypointDTO> result = waypointService.findByUserAndCity(userId, city, country);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }


    @Test
    void testRemoveWaypointsByUserAndCityAndCountry_ShouldRemoveAndSave() {
        // Arrange
        Long userId = 1L;
        String city = "Milano";
        String country = "IT";

        // Lista modificabile
        Set<Waypoint> waypoints = new HashSet<>();
        waypoints.add(createWaypoint(1L, "Milano", "IT")); // Da rimuovere
        waypoints.add(createWaypoint(2L, "Roma", "IT"));   // Da tenere

        User user = mock(User.class);
        when(userService.findById(userId)).thenReturn(user);
        when(user.getSavedWaypoints()).thenReturn(waypoints);

        // Act
        waypointService.removeWaypointsByUserAndCityAndCountry(userId, city, country);

        // Assert
        assertEquals(1, waypoints.size());
        assertEquals("Roma", waypoints.iterator().next().getDestination()); // È rimasto solo Roma
        verify(userService).saveUser(user);
    }

    // --- Helper ---
    
    private Waypoint createWaypoint(Long id, String dest, String country) {
        Waypoint w = new Waypoint();
        w.setId(id);
        w.setDestination(dest);
        w.setCountry(country);
        w.setName("Wp-" + id);
        w.setLocation(new Location(0.0,0.0));
        return w;
    }
}
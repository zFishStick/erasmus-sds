package com.sds2.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import com.sds2.classes.request.POIRequest;
import com.sds2.classes.request.UserRequest;
import com.sds2.dto.RouteDTO;
import com.sds2.dto.UserDTO;
import com.sds2.service.RoutesService;
import com.sds2.service.UserService;
import com.sds2.service.WaypointService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private WaypointService waypointService;

    @MockitoBean
    private RoutesService routesService;

    /* =========================
       GET /user/register
       ========================= */
    @Test
    void registerPage_returnsRegisterView() throws Exception {
        mockMvc.perform(get("/user/register"))
            .andExpect(status().isOk())
            .andExpect(view().name("register"));
    }

    /* =========================
       POST /user/register
       ========================= */
    @Test
    void registerUser_success_redirectsToUser() throws Exception {
        UserDTO dto = new UserDTO(1L, "test@mail.com", "Test");

        when(userService.registerUser(any(UserRequest.class)))
            .thenReturn(dto);

        mockMvc.perform(post("/user/register"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user"));
    }

    @Test
    void registerUser_failure_redirectsWithError() throws Exception {
        when(userService.registerUser(any(UserRequest.class)))
            .thenReturn(null);

        mockMvc.perform(post("/user/register"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/register?error=User already exists"));
    }

    /* =========================
       GET /user/login
       ========================= */
    @Test
    void loginPage_returnsLoginView() throws Exception {
        mockMvc.perform(get("/user/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
    }

    /* =========================
       GET /user/status
       ========================= */
    @Test
    void loginStatus_loggedOut() throws Exception {
        mockMvc.perform(get("/user/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.loggedIn").value(false));
    }

    @Test
    void loginStatus_loggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new UserDTO(1L, "mail", "name"));

        mockMvc.perform(get("/user/status").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.loggedIn").value(true));
    }

    /* =========================
       GET /user
       ========================= */
    @Test
    void getUser_notLogged_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/user"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    void getUser_logged_returnsUserView() throws Exception {
        UserDTO user = new UserDTO(1L, "mail", "name");

        when(userService.getUserRoutes(1L))
            .thenReturn(List.of(
                new RouteDTO("r1", "Rome", "IT", "OriginA", "DestinationA", List.of("Point1", "Point2"), "DRIVING"),
                new RouteDTO("r2", "Rome", "IT", "OriginB", "DestinationB", List.of("Point3", "Point4"), "WALKING")
            ));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(get("/user").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("user"))
            .andExpect(model().attributeExists("itinerariesByLocation"));
    }

    /* =========================
       GET /user/itineraries
       ========================= */
    @Test
    void getItineraries_noRequest_redirectsHome() throws Exception {
        mockMvc.perform(get("/user/itineraries"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }

    @Test
    void getItineraries_loggedIn_success() throws Exception {
        UserDTO user = new UserDTO(1L, "mail", "name");
        POIRequest req = new POIRequest("Rome", "IT",
            41.9028,
            12.4964,
            "2024-07-01",
            "2024-07-10"
        );

        when(waypointService.findByUserAndCity(1L, "Rome", "IT"))
            .thenReturn(List.of());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        session.setAttribute(UserController.REQUEST, req);

        mockMvc.perform(get("/user/itineraries").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("itineraries"))
            .andExpect(model().attributeExists("waypoints"));
    }

    /* =========================
       GET /user/itinerary/{id}
       ========================= */
    @Test
    void getItinerary_notLogged_redirectsLogin() throws Exception {
        mockMvc.perform(get("/user/itinerary/abc"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/login"));
    }

    /* =========================
       GET /user/logout
       ========================= */
    @Test
    void logout_invalidatesSession() throws Exception {
        mockMvc.perform(get("/user/logout"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }

    /* =========================
       POST /user/waypoint/remove/{id}
       ========================= */
    @Test
    void removeWaypoint_notLogged_redirectsLogin() throws Exception {
        mockMvc.perform(post("/user/waypoint/remove/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    void removeWaypoint_logged_success() throws Exception {
        UserDTO user = new UserDTO(1L, "mail", "name");

        when(userService.removeWaypointFromUser(1L, 1L))
            .thenReturn("Waypoint removed");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(post("/user/waypoint/remove/1").session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/itineraries"));
    }
}

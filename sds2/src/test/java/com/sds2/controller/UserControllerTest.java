package com.sds2.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sds2.classes.entity.Route;
import com.sds2.classes.entity.User;
import com.sds2.classes.entity.Waypoint;
import com.sds2.classes.enums.RouteTravelMode;
import com.sds2.classes.request.POIRequest;
import com.sds2.classes.request.UserRequest;
import com.sds2.classes.response.LoginResponse;
import com.sds2.dto.RouteDTO;
import com.sds2.dto.UserDTO;
import com.sds2.dto.WaypointDTO;
import com.sds2.service.RoutesService;
import com.sds2.service.UserService;
import com.sds2.service.WaypointService;
import com.sds2.util.PasswordManager;

import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private WaypointService waypointService;

    @Mock
    private RoutesService routesService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    @Test
    void testRegister() {
        String viewName = userController.register();
        assertEquals("register", viewName);
    }

    @Test
    void testRegisterUser_Success() {
        UserRequest request = new UserRequest();
        UserDTO userDTO = new UserDTO(1L, "test", "test@email.com");

        when(userService.registerUser(any(UserRequest.class))).thenReturn(userDTO);

        String viewName = userController.registerUser(request, session);

        verify(session).setAttribute("user", userDTO);
        assertEquals("redirect:/user", viewName);
    }

    @Test
    void testRegisterUser_Failure() {
        UserRequest request = new UserRequest();
        when(userService.registerUser(any(UserRequest.class))).thenReturn(null);

        String viewName = userController.registerUser(request, session);

        verify(session, never()).setAttribute(anyString(), any());
        assertEquals("redirect:/register?error=User already exists", viewName);
    }

    @Test
    void testLoginAjax_UserNotFound() {
        UserDTO userDTO = new UserDTO(1L, "test", "test@email.com");
        when(userService.getUserByEmail(anyString())).thenReturn(userDTO);
        when(userService.findById(anyLong())).thenReturn(null);

        LoginResponse response = userController.loginAjax("test@email.com", "password", null, null, session);

        assertFalse(response.isSuccess());
        assertEquals(LoginResponse.LoginStatus.USER_NOT_FOUND, response.getMessage());
    }

    @Test
    void testLoginAjax_InvalidCredentials() {
        UserDTO userDTO = new UserDTO(1L, "test", "test@email.com");
        User user = new User();
        user.setPassword("hashedPassword");
        
        when(userService.getUserByEmail(anyString())).thenReturn(userDTO);
        when(userService.findById(anyLong())).thenReturn(user);

        try (MockedStatic<PasswordManager> passwordManagerMock = mockStatic(PasswordManager.class)) {
            passwordManagerMock.when(() -> PasswordManager.verifyPassword(anyString(), anyString())).thenReturn(false);

            LoginResponse response = userController.loginAjax("test@email.com", "wrongPassword", null, null, session);

            assertFalse(response.isSuccess());
            assertEquals(LoginResponse.LoginStatus.INVALID_CREDENTIALS, response.getMessage());
        }
    }

    @Test
    void testLoginAjax_Success() {
        String email = "test@email.com";
        String password = "password";
        UserDTO userDTO = new UserDTO(1L, "test", email);
        User user = new User();
        user.setPassword("hashedPassword");

        when(userService.getUserByEmail(email)).thenReturn(userDTO);
        when(userService.findById(1L)).thenReturn(user);

        try (MockedStatic<PasswordManager> passwordManagerMock = mockStatic(PasswordManager.class)) {
            passwordManagerMock.when(() -> PasswordManager.verifyPassword(password, "hashedPassword")).thenReturn(true);

            LoginResponse response = userController.loginAjax(email, password, "Rome", "IT", session);

            assertTrue(response.isSuccess());
            assertEquals(LoginResponse.LoginStatus.SUCCESS, response.getMessage());
            assertEquals("/user", response.getRedirectUrl());
            
            verify(session).setAttribute("user", userDTO);
            verify(session).setAttribute(UserController.DESTINATION, "Rome");
            verify(session).setAttribute(UserController.COUNTRY_CODE, "IT");
        }
    }

    @Test
    void testLoginPage() {
        String destination = "Paris";
        String countryCode = "FR";

        String viewName = userController.loginPage(destination, countryCode, model);

        verify(model).addAttribute(UserController.DESTINATION, destination);
        verify(model).addAttribute(UserController.COUNTRY_CODE, countryCode);
        assertEquals("login", viewName);
    }

    @Test
    void testLoginStatus_LoggedIn() {
        UserDTO userDTO = new UserDTO(1L, "test", "test@email.com");
        when(session.getAttribute("user")).thenReturn(userDTO);

        Map<String, Object> response = userController.loginStatus(session);

        assertTrue((Boolean) response.get("loggedIn"));
        assertEquals(userDTO, response.get("user"));
    }

    @Test
    void testLoginStatus_LoggedOut() {
        when(session.getAttribute("user")).thenReturn(null);

        Map<String, Object> response = userController.loginStatus(session);

        assertFalse((Boolean) response.get("loggedIn"));
        assertEquals(null, response.get("user"));
    }

    @Test
    void testGetUserFromSession_NotLoggedIn() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = userController.getUserFromSession(session, model);

        assertEquals(UserController.REDIRECT, viewName);
    }

    @Test
    void testGetUserFromSession_Success() {
        UserDTO userDTO = new UserDTO(1L, "test", "test@email.com");
        POIRequest poiRequest = new POIRequest();
        poiRequest.setDestination("Berlin");
        poiRequest.setCountryCode("DE");
        
        List<RouteDTO> routes = new ArrayList<>();
        RouteDTO routeDTO = new RouteDTO(
                    "Route1",             
                    "Berlin",  
                    "Germany",              
                    "StartPoint",
                    "EndPoint",       
                    Collections.emptyList(),
                    "car"                
                );
        routes.add(routeDTO);

        when(session.getAttribute("user")).thenReturn(userDTO);
        when(userService.getUserRoutes(1L)).thenReturn(routes);
        when(session.getAttribute(UserController.REQUEST)).thenReturn(poiRequest);

        String viewName = userController.getUserFromSession(session, model);

        verify(model).addAttribute(UserController.DESTINATION, "Berlin");
        verify(model).addAttribute(UserController.COUNTRY_CODE, "DE");
        verify(model).addAttribute(eq("itinerariesByLocation"), any(Map.class));
        verify(model).addAttribute("user", userDTO);
        assertEquals("user", viewName);
    }

    @Test
    void testLogout() {
        String viewName = userController.logout(session);
        verify(session).invalidate();
        assertEquals("redirect:/", viewName);
    }

    @Test
    void testLoadUserItineraries() {
        String destination = "Madrid";
        String countryCode = "ES";

        String viewName = userController.loadUserItineraries(destination, countryCode, session);

        verify(session).setAttribute(UserController.DESTINATION, destination);
        verify(session).setAttribute(UserController.COUNTRY_CODE, countryCode);
        assertEquals("redirect:/user/itineraries", viewName);
    }

    @Test
    void testGetUserItineraries_NoUser() {
        POIRequest request = new POIRequest();
        request.setDestination("Rome");
        request.setCountryCode("IT");

        when(session.getAttribute(UserController.REQUEST)).thenReturn(request);
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = userController.getUserItineraries(session, model);

        verify(session).setAttribute(UserController.DESTINATION, "Rome");
        verify(session).setAttribute(UserController.COUNTRY_CODE, "IT");
        assertEquals(UserController.REDIRECT, viewName);
    }

    @Test
    void testGetUserItineraries_Success() {
        UserDTO userDTO = new UserDTO(1L, "test", "test@email.com");
        POIRequest request = new POIRequest();
        request.setDestination("Rome");
        request.setCountryCode("IT");
        List<WaypointDTO> waypoints = Collections.emptyList();

        when(session.getAttribute("user")).thenReturn(userDTO);
        when(session.getAttribute(UserController.REQUEST)).thenReturn(request);
        when(waypointService.findByUserAndCity(1L, "Rome", "IT")).thenReturn(waypoints);

        String viewName = userController.getUserItineraries(session, model);

        verify(model).addAttribute("waypoints", waypoints);
        verify(model).addAttribute("user", userDTO);
        assertEquals("itineraries", viewName);
    }

    @Test
    void testGetItineraryByRouteIdentifier_NoUser() {
        when(session.getAttribute("user")).thenReturn(null);
        String viewName = userController.getItineraryByRouteIdentifier("route123", session, model);
        assertEquals(UserController.REDIRECT, viewName);
    }

    @Test
    void testGetItineraryByRouteIdentifier_Success() {
        UserDTO userDTO = new UserDTO(1L, "test", "test@email.com");
        String routeId = "route123";
        
        Route route = new Route();
        route.setId(1L);
        route.setRouteIdentifier(routeId);
        route.setCity("London");
        route.setCountry("UK");
        
        Waypoint origin = new Waypoint();
        origin.setName("Origin");
        Waypoint dest = new Waypoint();
        dest.setName("Dest");
        
        route.setOrigin(origin);
        route.setDestination(dest);
        route.setIntermediates(new ArrayList<>());
        route.setTravelMode(RouteTravelMode.DRIVING);

        when(session.getAttribute("user")).thenReturn(userDTO);
        when(routesService.getRouteByRouteIdentifier(routeId)).thenReturn(route);

        String viewName = userController.getItineraryByRouteIdentifier(routeId, session, model);

        verify(model).addAttribute(eq("route"), any(RouteDTO.class));
        verify(model).addAttribute(eq("waypoints"), any(List.class));
        verify(model).addAttribute("user", userDTO);
        assertEquals("itineraryDetails", viewName);
    }

    @Test
    void testDeleteItineraryByRouteIdentifier_NoUser() {
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(session.getAttribute("user")).thenReturn(null);
        
        String viewName = userController.deleteItineraryByRouteIdentifier("route1", session, redirectAttributes);
        
        assertEquals(UserController.REDIRECT, viewName);
    }

    @Test
    void testDeleteItineraryByRouteIdentifier_Success() {
        UserDTO userDTO = new UserDTO(1L, "test", "test@email.com");
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String routeId = "route1";
        
        when(session.getAttribute("user")).thenReturn(userDTO);
        when(routesService.deleteRouteByRouteIdentifier(routeId)).thenReturn("Deleted successfully");

        String viewName = userController.deleteItineraryByRouteIdentifier(routeId, session, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("message", "Deleted successfully");
        assertEquals("redirect:/user", viewName);
    }

    @Test
    void testRemoveWaypoint_NoUser() {
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = userController.removeWaypoint(1L, session, redirectAttributes);

        assertEquals(UserController.REDIRECT, viewName);
    }

    @Test
    void testRemoveWaypoint_Success() {
        UserDTO userDTO = new UserDTO(1L, "test", "test@email.com");
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Long waypointId = 100L;

        when(session.getAttribute("user")).thenReturn(userDTO);
        when(userService.removeWaypointFromUser(waypointId, 1L)).thenReturn("Removed");

        String viewName = userController.removeWaypoint(waypointId, session, redirectAttributes);

        verify(userService).removeWaypointFromUser(waypointId, 1L);
        verify(redirectAttributes).addFlashAttribute("message", "Removed");
        assertEquals("redirect:/user/itineraries", viewName);
    }
}
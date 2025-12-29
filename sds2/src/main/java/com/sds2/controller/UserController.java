package com.sds2.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sds2.classes.request.UserRequest;
import com.sds2.classes.response.LoginResponse;
import com.sds2.dto.UserDTO;
import com.sds2.dto.WaypointDTO;
import com.sds2.service.UserService;
import com.sds2.service.WaypointService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final WaypointService waypointService;

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(UserRequest userRequest, HttpSession session) {
        UserDTO user = userService.registerUser(userRequest);

        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/user";
        } else {
            return "redirect:/register?error=User already exists";
        }

    }
    
    @PostMapping("/login")
    @ResponseBody
    public LoginResponse loginUser(String email, String password, HttpSession session) {
        LoginResponse loginResult = userService.loginUser(email, password);
        if (loginResult.isSuccess()) {
            UserDTO user = userService.getUserByEmail(email);
            session.setAttribute("user", user);
        }
        return loginResult;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/status")
    @ResponseBody
    public Map<String, Object> loginStatus(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        response.put("loggedIn", user != null);
        response.put("user", user);

        return response;
    }

    @GetMapping
    public String getUserFromSession(HttpSession session, Model model) {

        UserDTO user = (UserDTO) session.getAttribute("user");

        if (user == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("user", user);
        return "user";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/itineraries")
    public String getUserItineraries(
            @RequestParam String city,
            @RequestParam(required = false) String countryCode,
            HttpSession session,
            Model model) {

        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

        List<WaypointDTO> waypoints =
            waypointService.findByUserAndCity(
                user.id(),
                city,
                countryCode
            );

        model.addAttribute("city", city);
        model.addAttribute("countryCode", countryCode);
        model.addAttribute("waypoints", waypoints);
        model.addAttribute("user", user);

        return "itineraries";
    }


    
    
}

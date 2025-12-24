package com.sds2.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sds2.classes.request.UserRequest;
import com.sds2.classes.response.LoginResponse;
import com.sds2.dto.UserDTO;
import com.sds2.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(UserRequest userRequest, HttpSession session) {
        UserDTO user = userService.registerUser(userRequest);

        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/user/" + user.id();
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

    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id, HttpSession session, Model model) {

        if (session.getAttribute("user") != null) {
            model.addAttribute("user", session.getAttribute("user"));
            return "user";
        }

        UserDTO user = userService.getUserById(id);
        session.setAttribute("user", user);
        model.addAttribute("user", user);
        return "user";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/itineraries/{userId}")
    public String getUserItineraries(@PathVariable Long userId, Model model, HttpSession session) {
        UserDTO sessionUser = (UserDTO) session.getAttribute("user");
        if (sessionUser == null || !sessionUser.id().equals(userId)) {
            return "redirect:/user/login";
        }

        UserDTO user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("itineraries", userService.getUserRoutes(userId));
        return "itineraries";
    }
    
    
}

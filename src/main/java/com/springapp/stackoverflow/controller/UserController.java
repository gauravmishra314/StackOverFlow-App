package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.dto.UserDTO.LoginRequest;
import com.springapp.stackoverflow.dto.UserDTO.PasswordUpdateRequest;
import com.springapp.stackoverflow.dto.UserDTO.SignupRequest;
import com.springapp.stackoverflow.dto.UserDTO.UserUpdateRequest;
import com.springapp.stackoverflow.model.User;
import com.springapp.stackoverflow.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // Display the login page
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    // Process login form submission
    @PostMapping("/login")
    public String processLogin(@ModelAttribute LoginRequest loginRequest,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = userService.login(loginRequest);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            return "redirect:/questions";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    // Display the signup page
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    // Process signup form submission
    @PostMapping("/signup")
    public String processSignup(@ModelAttribute SignupRequest signupRequest,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.signup(signupRequest);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // Display user profile
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("updateRequest", new UserUpdateRequest());
        return "profile";
    }

    // Update user profile
    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @ModelAttribute UserUpdateRequest updateRequest,
                                RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            userService.updateUserProfile(userId, updateRequest);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    // Change password page
    @GetMapping("/change-password")
    public String showChangePasswordPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("passwordRequest", new PasswordUpdateRequest());
        return "change-password";
    }

    // Process password change
    @PostMapping("/change-password")
    public String changePassword(HttpSession session,
                                 @ModelAttribute PasswordUpdateRequest passwordRequest,
                                 RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            userService.updatePassword(userId, passwordRequest);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully");
            return "redirect:/profile";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/change-password";
        }
    }

    // Dashboard page
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "dashboard";
    }
}
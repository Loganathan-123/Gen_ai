package com.exam.controller;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("user") User user, Model model, HttpSession session) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
            session.setAttribute("userId", existingUser.getId());
            session.setAttribute("username", existingUser.getUsername());
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username already exists");
            return "signup";
        }

        userRepository.save(user);
        return "redirect:/login?success";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}